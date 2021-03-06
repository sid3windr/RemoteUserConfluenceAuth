/**
 * Copyright 2015 Geert Lorang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package glorang.confluence;

import org.apache.log4j.Category;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.atlassian.confluence.user.ConfluenceAuthenticator;

public class RemoteUserConfluenceAuth extends ConfluenceAuthenticator
{
    private static final Category log = Category.getInstance(RemoteUserConfluenceAuth.class);

    public Principal getUser(HttpServletRequest request, HttpServletResponse response)
    {
        Principal user = null;
        try
        {
            if(request.getSession() != null && request.getSession().getAttribute(ConfluenceAuthenticator.LOGGED_IN_KEY) != null)
            {
                log.debug("Session found; user already logged in");
                user = (Principal) request.getSession().getAttribute(ConfluenceAuthenticator.LOGGED_IN_KEY);
            }
            else
            {
                    log.debug("Trying RemoteUserConfluenceAuth SSO");
                    String remoteuser = request.getRemoteUser();
                    log.debug("remote_user set to: " + remoteuser);
                    if(remoteuser != null)
                    {
                        String[] username = remoteuser.split("@");
                        user = getUser(username[0]);
                        log.debug("Logging in with username: " + user);
                        request.getSession().setAttribute(ConfluenceAuthenticator.LOGGED_IN_KEY, user);
                        request.getSession().setAttribute(ConfluenceAuthenticator.LOGGED_OUT_KEY, null);
                    }
                    else
                    {
                        log.warn("remote_user is null");
                        return null;
                    }
            }
        }
        catch (Exception e)
        {
            log.warn("Exception: " + e, e);
        }
        return user;
    }

}