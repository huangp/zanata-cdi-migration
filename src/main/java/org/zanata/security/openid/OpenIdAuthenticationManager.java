/*
 * Copyright 2014, Red Hat, Inc. and individual contributors as indicated by the
 * @author tags. See the copyright.txt file in the distribution for a full
 * listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.zanata.security.openid;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openid4java.OpenIDException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.ParameterList;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.ax.FetchRequest;
import org.openid4java.message.ax.FetchResponse;
import org.picketlink.Identity;
import org.zanata.security.authenticator.AuthenticatorSelector;
import org.zanata.security.credentials.OpenIdCredentials;

/**
 * @author Carlos Munoz <a
 *         href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
@SessionScoped
@Named("openIdAuthenticator")
public class OpenIdAuthenticationManager implements Serializable {

    private ConsumerManager manager;

    @Inject
    private OpenIdCredentials credentials;

    @Inject
    private Identity identity;

    @Inject
    private transient AuthenticatorSelector authenticatorSelector;

    @PostConstruct
    private void initialize() {
        manager = new ConsumerManager();
    }

    public void initiateAuthentication(String openId,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        try
        {
            // configure the return_to URL where your application will receive
            // the authentication responses from the OpenID provider
            String returnToUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath()
                    + "/openid/return";

            // --- Forward proxy setup (only if needed) ---
            // ProxyProperties proxyProps = new ProxyProperties();
            // proxyProps.setProxyName("proxy.example.com");
            // proxyProps.setProxyPort(8080);
            // HttpClientFactory.setProxyProperties(proxyProps);

            // perform discovery on the user-supplied identifier
            List discoveries = manager.discover(openId);

            // attempt to associate with the OpenID provider
            // and retrieve one service endpoint for authentication
            DiscoveryInformation discovered = manager.associate(discoveries);

            // store the discovery information in the user's session
            request.getSession().setAttribute("openid-disc", discovered);

            // obtain a AuthRequest message to be sent to the OpenID provider
            AuthRequest authReq = manager.authenticate(discovered, returnToUrl);

            // Attribute Exchange example: fetching the 'email' attribute
            FetchRequest fetch = FetchRequest.createFetchRequest();
            fetch.addAttribute("email",
                    // attribute alias
                    "http://schema.openid.net/contact/email", // type URI
                    true); // required

            // attach the extension to the authentication request
            authReq.addExtension(fetch);

            // TODO For now, using OpenId 1.x to get it going
            if (/*!discovered.isVersion2()*/true)
            {
                // Option 1: GET HTTP-redirect to the OpenID Provider endpoint
                // The only method supported in OpenID 1.x
                // redirect-URL usually limited ~2048 bytes
                response.sendRedirect(authReq.getDestinationUrl(true));
            }
            else
            {
                // Option 2: HTML FORM Redirection (Allows payloads >2048 bytes)

                RequestDispatcher dispatcher =
                        request.getServletContext().getRequestDispatcher(
                                "formredirection.jsp");
                request.setAttribute("parameterMap",
                        authReq.getParameterMap());
                request.setAttribute("destinationUrl",
                        authReq.getDestinationUrl(false));
                dispatcher.forward(request, response);
            }
        } catch (OpenIDException e)
        {
            // present error to the user
        }
    }

    // TODO handle exceptions properly
    public void verifyResponse(HttpServletRequest request,
            HttpServletResponse httpResponse) throws Exception {
        try
        {
            // extract the parameters from the authentication response
            // (which comes in as a HTTP request from the OpenID provider)
            ParameterList response =
                    new ParameterList(request.getParameterMap());

            // retrieve the previously stored discovery information
            DiscoveryInformation discovered = (DiscoveryInformation)
                    request.getSession().getAttribute("openid-disc");

            // extract the receiving URL from the HTTP request
            StringBuffer receivingURL = request.getRequestURL();
            String queryString = request.getQueryString();
            if (queryString != null && queryString.length() > 0)
                receivingURL.append("?").append(request.getQueryString());

            // verify the response; ConsumerManager needs to be the same
            // (static) instance used to place the authentication request
            VerificationResult verification = manager.verify(
                    receivingURL.toString(),
                    response, discovered);

            // examine the verification result and extract the verified
            // identifier
            Identifier verified = verification.getVerifiedId();
            if (verified != null)
            {
                AuthSuccess authSuccess =
                        (AuthSuccess) verification.getAuthResponse();

                if (authSuccess.hasExtension(AxMessage.OPENID_NS_AX))
                {
                    FetchResponse fetchResp = (FetchResponse) authSuccess
                            .getExtension(AxMessage.OPENID_NS_AX);

                    List emails = fetchResp.getAttributeValues("email");
                    String email = (String) emails.get(0);
                }

                // return verified; // success
                // Success
                logInIfSuccessful(verification);
            }
        } catch (OpenIDException e)
        {
            // present error to the user
        }
    }

    private void logInIfSuccessful(VerificationResult verification) {
        credentials.setOpenId(verification.getVerifiedId().getIdentifier());
        credentials.setAuthenticatedByProvider(true);
        authenticatorSelector.setCredentials(credentials);
        identity.login();
    }
}
