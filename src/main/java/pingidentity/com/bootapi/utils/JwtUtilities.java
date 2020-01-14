package pingidentity.com.bootapi.utils;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;

public class JwtUtilities {

	protected final static Log logger = LogFactory.getLog(JwtUtilities.class);

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String getUserPrincipal(String principalAttr, String jwt, String jwksUrl)
	{
		logger.info("principalAttr = " + principalAttr);
		logger.info("getUserPrincipal:JWT = " + jwt);
		logger.info("getUserPrincipal:jwksUrl = " + jwksUrl);
		
		// Set up a JWT processor to parse the tokens and then check their
		// signature
		// and validity time window (bounded by the "iat", "nbf" and "exp"
		// claims)
		ConfigurableJWTProcessor jwtProcessor = new DefaultJWTProcessor();

		// The public RSA keys to validate the signatures will be sourced
		// from the
		// OAuth 2.0 server's JWK set, published at a well-known URL. The
		// RemoteJWKSet
		// object caches the retrieved keys to speed up subsequent look-ups
		// and can
		// also gracefully handle key-rollover
		JWKSource keySource = null;
		
		try {
			keySource = new RemoteJWKSet(new URL(
					jwksUrl));
		} catch (MalformedURLException e) {
			logger.error("Malformed URL", e);
			return null;
		}
		
		// The expected JWS algorithm of the access tokens (agreed out-of-band)
		JWSAlgorithm expectedJWSAlg = JWSAlgorithm.ES256;

		// Configure the JWT processor with a key selector to feed matching public
		// RSA keys sourced from the JWK set URL
		JWSKeySelector keySelector = new JWSVerificationKeySelector(expectedJWSAlg, keySource);
		jwtProcessor.setJWSKeySelector(keySelector);

		// Process the token
		SecurityContext ctx = null; // optional context parameter, not required here
		JWTClaimsSet claimsSet;
		try {
			claimsSet = jwtProcessor.process(jwt, ctx);
		} catch (Exception e) {
			logger.error("Could not process JWT", e);
			return null;
		}
		
		return claimsSet.getClaim(principalAttr).toString();
	}
}
