package pingidentity.com.bootapi.utils;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.util.StringUtils;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.csrf().disable();
		http.addFilter(
				new JwtHeaderPreAuthenticator(this.authenticationManager()))
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
					.and()
				.authorizeRequests()
				.anyRequest().authenticated();

	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth)
			throws Exception {

		auth.authenticationProvider(new JwtHeaderPreProvider());
	}

	class JwtHeaderPreProvider implements AuthenticationProvider {

		@Override
		public Authentication authenticate(Authentication authentication)
				throws AuthenticationException {
			authentication.setAuthenticated(true);

			return authentication;
		}

		@Override
		public boolean supports(Class<?> authentication) {
			return authentication
					.equals(PreAuthenticatedAuthenticationToken.class);
		}
	}

	class JwtHeaderPreAuthenticator extends
			AbstractPreAuthenticatedProcessingFilter {

		JwtHeaderPreAuthenticator(AuthenticationManager authMgr) {
			this.setInvalidateSessionOnPrincipalChange(true);
			this.setAuthenticationManager(authMgr);
		}

		@Override
		protected Object getPreAuthenticatedCredentials(
				HttpServletRequest request) {
			return null;
		}

		@Override
		protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {

			logger.info("getPreAuthenticatedPrincipal");
			String jwt = request.getHeader("pajwt");

			// TODO Bad stuff here. Only because this is a POC.
			try {
				Tools.disableSSLCertificateChecking();
			} catch (Exception e) {
			}
			if (StringUtils.isEmpty(jwt))
				jwt = request.getParameter("pajwt");
			// End bad stuff

			if (StringUtils.hasLength(jwt)) {
				logger.info("getPreAuthenticatedPrincipal:Has JWT");

				String paJWKSUrl = "";
				
				String paJWKSUrlHeader = request.getHeader("X-PA-JWKS-ENDPOINT");
				
				if(!StringUtils.isEmpty(paJWKSUrlHeader))
				{
					paJWKSUrl = String.format("%s/pa/authtoken/JWKS",
							paJWKSUrlHeader);
				}
				else
				{
				
					String scheme = StringUtils.isEmpty(request
							.getHeader("X-Forwarded-Proto")) ? request.getScheme()
							: request.getHeader("X-Forwarded-Proto");

					String serverName = request.getServerName();
					int serverPort = request.getServerPort();
					String host = StringUtils.isEmpty(request.getHeader("host")) ? String
							.format("%s:%s", serverName, serverPort) : request
							.getHeader("host");
				
					paJWKSUrl = String.format("%s://%s/pa/authtoken/JWKS",
							scheme, host);
				}

				logger.info("getPreAuthenticatedPrincipal:JWKS URL: " + paJWKSUrl);

				String principal = JwtUtilities.getUserPrincipal("sub", jwt,
						paJWKSUrl);
				
				logger.info("getPreAuthenticatedPrincipal:Principal: " + principal);

				if (StringUtils.isEmpty(principal))
					return null;

				Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
				return new User(principal, "autologin", true, true, true, true,
						authorities);
			} else
			{
				logger.info("getPreAuthenticatedPrincipal:Has no JWT");
				return null;
			}
		}

	}
}