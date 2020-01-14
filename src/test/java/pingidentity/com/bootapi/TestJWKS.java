package pingidentity.com.bootapi;

import pingidentity.com.bootapi.utils.JwtUtilities;
import junit.framework.TestCase;

public class TestJWKS extends TestCase {

	public void testJWKS()
	{
		String jwt = "eyJraWQiOiJhIiwiYWxnIjoiRVMyNTYifQ.eyJpc3MiOiJQaW5nQWNjZXNzQXV0aFRva2VuIiwic3ViIjoidHRyYW5AcGluZ2lkZW50aXR5LmNvbSIsImF1ZCI6InBhand0IiwiZXhwIjoxNDc1NjQ0NDE3LCJpYXQiOjE0NzU2MzcyMTN9.JIEIe_0jCmf7eBQFX03OX3k-c8x9-JrNwVPbXqyCUEHbLVCEZMyauaNbMOGK8eEPUNu4gNT1lxlPMGcYxnJxXw";
		
		String jwksUrl = "https://demoapi.pingapac.com/pa/authtoken/JWKS";
		
		System.out.println(JwtUtilities.getUserPrincipal("sub", jwt, jwksUrl));
		
		
	}
}
