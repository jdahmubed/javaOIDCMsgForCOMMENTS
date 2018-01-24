// Copyright (c) 2017 The Authors of 'JWTS for Java'
//
// Permission is hereby granted, free of charge, to any person obtaining a copy of
// this software and associated documentation files (the "Software"), to deal in
// the Software without restriction, including without limitation the rights to
// use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
// the Software, and to permit persons to whom the Software is furnished to do so,
// subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
// FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
// COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
// IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
// CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package com.auth0.jwt.creators;

import static com.auth0.jwt.TimeUtil.generateRandomExpDateInFuture;
import static com.auth0.jwt.TimeUtil.generateRandomIatDateInPast;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.impl.PublicClaims;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.GoogleVerification;
import com.auth0.jwt.jwts.GoogleJWT;
import com.auth0.jwt.jwts.JWT;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class GoogleJwtCreatorTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private static final Date exp = generateRandomExpDateInFuture();
    private static final Date iat = generateRandomIatDateInPast();
    public static final String PICTURE = "picture";
    public static final String EMAIL = "email";
    public static final String NAME = "name";


    @Test
    public void testGoogleJwtCreatorAllStandardClaimsMustBeRequired() throws Exception {
        Algorithm algorithm = Algorithm.HMAC256("secret");
        String token = GoogleJwtCreator.build()
                .withPicture(PICTURE)
                .withEmail(EMAIL)
                .withIssuer("issuer")
                .withSubject("subject")
                .withAudience("audience")
                .withExp(exp)
                .withIat(iat)
                .withName(NAME)
                .sign(algorithm);
        GoogleVerification verification = GoogleJWT.require(algorithm);
        JWT verifier = verification.createVerifierForGoogle(PICTURE, EMAIL, asList("issuer"), asList("audience"),
                NAME, 1, 1).build();
        DecodedJWT jwt = verifier.decode(token);
        Map<String, Claim> claims = jwt.getClaims();
        verifyClaims(claims, exp);
    }

    @Test
    public void testGoogleJwtCreatorBase16Encoding() throws Exception {
        Algorithm algorithm = Algorithm.HMAC256("secret");
        String token = GoogleJwtCreator.build()
                .withPicture(PICTURE)
                .withEmail(EMAIL)
                .withIssuer("issuer")
                .withSubject("subject")
                .withAudience("audience")
                .withExp(exp)
                .withIat(iat)
                .withName(NAME)
                .signBase16Encoding(algorithm);
        GoogleVerification verification = GoogleJWT.require(algorithm);
        JWT verifier = verification.createVerifierForGoogle(PICTURE, EMAIL, asList("issuer"), asList("audience"),
                NAME, 1, 1).build();
        DecodedJWT jwt = verifier.decode16Bytes(token);
        Map<String, Claim> claims = jwt.getClaims();
        verifyClaims(claims, exp);
    }

    @Test
    public void testGoogleJwtCreatorBase32Encoding() throws Exception {
        Algorithm algorithm = Algorithm.HMAC256("secret");
        String token = GoogleJwtCreator.build()
                .withPicture(PICTURE)
                .withEmail(EMAIL)
                .withIssuer("issuer")
                .withSubject("subject")
                .withAudience("audience")
                .withExp(exp)
                .withIat(iat)
                .withName(NAME)
                .signBase32Encoding(algorithm);
        GoogleVerification verification = GoogleJWT.require(algorithm);
        JWT verifier = verification.createVerifierForGoogle(PICTURE, EMAIL, asList("issuer"), asList("audience"),
                NAME, 1, 1).build();
        DecodedJWT jwt = verifier.decode32Bytes(token);
        Map<String, Claim> claims = jwt.getClaims();
        verifyClaims(claims, exp);
    }

    @Test
    public void testGoogleJwtCreatorWhenCertainRequiredClaimIsntProvided() throws Exception {
        thrown.expect(Exception.class);
        thrown.expectMessage("Standard claim: Picture has not been set");

        Algorithm algorithm = Algorithm.HMAC256("secret");
        String token = GoogleJwtCreator.build()
                .withEmail(EMAIL)
                .withIssuer("issuer")
                .withSubject("subject")
                .withAudience("audience")
                .withExp(exp)
                .withIat(iat)
                .withName(NAME)
                .sign(algorithm);

        GoogleVerification verification = GoogleJWT.require(algorithm);
        JWT verifier = verification.createVerifierForGoogle(PICTURE, EMAIL, asList("issuer"), asList("audience"),
                NAME, 1, 1).build();
        DecodedJWT jwt = verifier.decode(token);
    }

    @Test
    public void testGoogleJwtCreatorNoneAlgorithmNotAllowed() throws Exception {
        thrown.expect(IllegalAccessException.class);
        thrown.expectMessage("None algorithm isn't allowed");

        Algorithm algorithm = Algorithm.none();
        String token = GoogleJwtCreator.build()
                .withPicture(PICTURE)
                .withEmail(EMAIL)
                .withIssuer("issuer")
                .withSubject("subject")
                .withAudience("audience")
                .withExp(exp)
                .withIat(iat)
                .withName(NAME)
                .setIsNoneAlgorithmAllowed(false)
                .sign(algorithm);

        GoogleVerification verification = GoogleJWT.require(algorithm);
        JWT verifier = verification.createVerifierForGoogle(PICTURE, EMAIL, asList("issuer"), asList("audience"),
                NAME, 1, 1).build();
        DecodedJWT jwt = verifier.decode(token);
    }

    @Test
    public void testGoogleJwtCreatorNoneAlgorithmNotSpecifiedButStillNotAllowed() throws Exception {
        thrown.expect(IllegalAccessException.class);
        thrown.expectMessage("None algorithm isn't allowed");

        Algorithm algorithm = Algorithm.none();
        String token = GoogleJwtCreator.build()
                .withPicture(PICTURE)
                .withEmail(EMAIL)
                .withIssuer("issuer")
                .withSubject("subject")
                .withAudience("audience")
                .withExp(exp)
                .withIat(iat)
                .withName(NAME)
                .sign(algorithm);

        GoogleVerification verification = GoogleJWT.require(algorithm);
        JWT verifier = verification.createVerifierForGoogle(PICTURE, EMAIL, asList("issuer"), asList("audience"),
                NAME, 1, 1).build();
        DecodedJWT jwt = verifier.decode(token);
    }

    @Test
    public void testGoogleJwtCreatorNoneAlgorithmAllowed() throws Exception {
        Algorithm algorithm = Algorithm.none();
        String token = GoogleJwtCreator.build()
                .withPicture(PICTURE)
                .withEmail(EMAIL)
                .withIssuer("issuer")
                .withSubject("subject")
                .withAudience("audience")
                .withExp(exp)
                .withIat(iat)
                .withName(NAME)
                .setIsNoneAlgorithmAllowed(true)
                .sign(algorithm);

        GoogleVerification verification = GoogleJWT.require(algorithm);
        JWT verifier = verification.createVerifierForGoogle(PICTURE, EMAIL, asList("issuer"), asList("audience"),
                NAME, 1, 1).build();
        DecodedJWT jwt = verifier.decode(token);
        Map<String, Claim> claims = jwt.getClaims();
        verifyClaims(claims, exp);
    }

    @Test
    public void testGoogleJwtCreatorArrayClaim() throws Exception {
        Algorithm algorithm = Algorithm.none();
        String token = GoogleJwtCreator.build()
                .withPicture(PICTURE)
                .withEmail(EMAIL)
                .withIssuer("issuer")
                .withSubject("subject")
                .withAudience("audience")
                .withExp(exp)
                .withIat(iat)
                .setIsNoneAlgorithmAllowed(true)
                .withArrayClaim("arrayKey", "arrayValue1", "arrayValue2")
                .withName(NAME)
                .sign(algorithm);

        GoogleVerification verification = GoogleJWT.require(algorithm);
        JWT verifier = verification.createVerifierForGoogle(PICTURE, EMAIL, asList("issuer"), asList("audience"),
                NAME, 1, 1).build();
        DecodedJWT jwt = verifier.decode(token);
        Map<String, Claim> claims = jwt.getClaims();
        verifyClaims(claims, exp);
    }

    @Test
    public void testGoogleJwtCreatorInvalidIssuer() throws Exception {
        thrown.expect(InvalidClaimException.class);
        thrown.expectMessage("The Claim 'iss' value doesn't match the required one.");

        Algorithm algorithm = Algorithm.none();
        String token = GoogleJwtCreator.build()
                .withPicture(PICTURE)
                .withEmail(EMAIL)
                .withIssuer("invalid")
                .withSubject("subject")
                .withAudience("audience")
                .withExp(exp)
                .withIat(iat)
                .setIsNoneAlgorithmAllowed(true)
                .withName(NAME)
                .sign(algorithm);

        GoogleVerification verification = GoogleJWT.require(algorithm);
        JWT verifier = verification.createVerifierForGoogle(PICTURE, EMAIL, asList("issuer"), asList("audience"),
                NAME, 1, 1).build();
        DecodedJWT jwt = verifier.decode(token);
    }

    @Test
    public void testGoogleJwtCreatorInvalidAudience() throws Exception {
        thrown.expect(InvalidClaimException.class);
        thrown.expectMessage("The Claim 'aud' value doesn't contain the required audience.");

        Algorithm algorithm = Algorithm.HMAC256("secret");
        String token = GoogleJwtCreator.build()
                .withPicture(PICTURE)
                .withEmail(EMAIL)
                .withIssuer("issuer")
                .withSubject("subject")
                .withAudience("invalid")
                .withExp(exp)
                .withIat(iat)
                .withName(NAME)
                .sign(algorithm);

        GoogleVerification verification = GoogleJWT.require(algorithm);
        JWT verifier = verification.createVerifierForGoogle(PICTURE, EMAIL, asList("issuer"), asList("audience"),
                NAME, 1, 1).build();
        DecodedJWT jwt = verifier.decode(token);
    }

    @Test
    public void testGoogleJwtCreatorInvalidPicture() throws Exception {
        thrown.expect(InvalidClaimException.class);
        thrown.expectMessage("The Claim 'picture' value doesn't match the required one.");

        Algorithm algorithm = Algorithm.HMAC256("secret");
        String token = GoogleJwtCreator.build()
                .withPicture("invalid")
                .withEmail(EMAIL)
                .withIssuer("issuer")
                .withSubject("subject")
                .withAudience("audience")
                .withExp(exp)
                .withIat(iat)
                .withName(NAME)
                .sign(algorithm);

        GoogleVerification verification = GoogleJWT.require(algorithm);
        JWT verifier = verification.createVerifierForGoogle(PICTURE, EMAIL, asList("issuer"), asList("audience"),
                NAME, 1, 1).build();
        DecodedJWT jwt = verifier.decode(token);
    }

    @Test
    public void testGoogleJwtCreatorInvalidEmail() throws Exception {
        thrown.expect(InvalidClaimException.class);
        thrown.expectMessage("The Claim 'email' value doesn't match the required one.");

        Algorithm algorithm = Algorithm.HMAC256("secret");
        String token = GoogleJwtCreator.build()
                .withPicture(PICTURE)
                .withEmail("invalid")
                .withIssuer("issuer")
                .withSubject("subject")
                .withAudience("audience")
                .withExp(exp)
                .withIat(iat)
                .withName(NAME)
                .sign(algorithm);

        GoogleVerification verification = GoogleJWT.require(algorithm);
        JWT verifier = verification.createVerifierForGoogle(PICTURE, EMAIL, asList("issuer"), asList("audience"),
                NAME, 1, 1).build();
        DecodedJWT jwt = verifier.decode(token);
    }

    @Test
    public void testGoogleJwtCreatorInvalidName() throws Exception {
        thrown.expect(InvalidClaimException.class);
        thrown.expectMessage("The Claim 'name' value doesn't match the required one.");

        Algorithm algorithm = Algorithm.HMAC256("secret");
        String token = GoogleJwtCreator.build()
                .withPicture(PICTURE)
                .withEmail(EMAIL)
                .withIssuer("issuer")
                .withSubject("subject")
                .withAudience("audience")
                .withExp(exp)
                .withIat(iat)
                .withName("invalid")
                .sign(algorithm);

        GoogleVerification verification = GoogleJWT.require(algorithm);
        JWT verifier = verification.createVerifierForGoogle(PICTURE, EMAIL, asList("issuer"), asList("audience"),
                NAME, 1, 1).build();
        DecodedJWT jwt = verifier.decode(token);
    }

    @Test
    public void testGoogleJwtCreatorNonStandardClaimString() throws Exception {
        Algorithm algorithm = Algorithm.HMAC256("secret");
        String token = GoogleJwtCreator.build()
                .withPicture(PICTURE)
                .withEmail(EMAIL)
                .withIssuer("issuer")
                .withSubject("subject")
                .withAudience("audience")
                .withExp(exp)
                .withIat(iat)
                .withName(NAME)
                .withNonStandardClaim("nonStandardClaim", "nonStandardClaimValue")
                .sign(algorithm);
        GoogleVerification verification = GoogleJWT.require(algorithm);
        JWT verifier = verification.createVerifierForGoogle(PICTURE, EMAIL, asList("issuer"), asList("audience"),
                NAME, 1, 1).build();
        DecodedJWT jwt = verifier.decode(token);
        Map<String, Claim> claims = jwt.getClaims();
        verifyClaims(claims, exp);
    }

    @Test
    public void testGoogleJwtCreatorNonStandardClaimBoolean() throws Exception {
        Algorithm algorithm = Algorithm.HMAC256("secret");
        String token = GoogleJwtCreator.build()
                .withPicture(PICTURE)
                .withEmail(EMAIL)
                .withIssuer("issuer")
                .withSubject("subject")
                .withAudience("audience")
                .withExp(exp)
                .withIat(iat)
                .withName(NAME)
                .withNonStandardClaim("nonStandardClaim", true)
                .sign(algorithm);
        GoogleVerification verification = GoogleJWT.require(algorithm);
        JWT verifier = verification.createVerifierForGoogle(PICTURE, EMAIL, asList("issuer"), asList("audience"),
                NAME, 1, 1).build();
        DecodedJWT jwt = verifier.decode(token);
        Map<String, Claim> claims = jwt.getClaims();
        verifyClaims(claims, exp);
    }

    @Test
    public void testGoogleJwtCreatorNonStandardClaimInteger() throws Exception {
        Algorithm algorithm = Algorithm.HMAC256("secret");
        String token = GoogleJwtCreator.build()
                .withPicture(PICTURE)
                .withEmail(EMAIL)
                .withIssuer("issuer")
                .withSubject("subject")
                .withAudience("audience")
                .withExp(exp)
                .withIat(iat)
                .withName(NAME)
                .withNonStandardClaim("nonStandardClaim", 999)
                .sign(algorithm);
        GoogleVerification verification = GoogleJWT.require(algorithm);
        JWT verifier = verification.createVerifierForGoogle(PICTURE, EMAIL, asList("issuer"), asList("audience"),
                NAME, 1, 1).build();
        DecodedJWT jwt = verifier.decode(token);
        Map<String, Claim> claims = jwt.getClaims();
        verifyClaims(claims, exp);
    }

    @Test
    public void testGoogleJwtCreatorNonStandardClaimLong() throws Exception {
        Algorithm algorithm = Algorithm.HMAC256("secret");
        String token = GoogleJwtCreator.build()
                .withPicture(PICTURE)
                .withEmail(EMAIL)
                .withIssuer("issuer")
                .withSubject("subject")
                .withAudience("audience")
                .withExp(exp)
                .withIat(iat)
                .withName(NAME)
                .withNonStandardClaim("nonStandardClaim", 999L)
                .sign(algorithm);
        GoogleVerification verification = GoogleJWT.require(algorithm);
        JWT verifier = verification.createVerifierForGoogle(PICTURE, EMAIL, asList("issuer"), asList("audience"),
                NAME, 1, 1).build();
        DecodedJWT jwt = verifier.decode(token);
        Map<String, Claim> claims = jwt.getClaims();
        verifyClaims(claims, exp);
    }

    @Test
    public void testGoogleJwtCreatorNonStandardClaimDouble() throws Exception {
        Algorithm algorithm = Algorithm.HMAC256("secret");
        String token = GoogleJwtCreator.build()
                .withPicture(PICTURE)
                .withEmail(EMAIL)
                .withIssuer("issuer")
                .withSubject("subject")
                .withAudience("audience")
                .withExp(exp)
                .withIat(iat)
                .withName(NAME)
                .withNonStandardClaim("nonStandardClaim", 9.99)
                .sign(algorithm);
        GoogleVerification verification = GoogleJWT.require(algorithm);
        JWT verifier = verification.createVerifierForGoogle(PICTURE, EMAIL, asList("issuer"), asList("audience"),
                NAME, 1, 1).build();
        DecodedJWT jwt = verifier.decode(token);
        Map<String, Claim> claims = jwt.getClaims();
        verifyClaims(claims, exp);
    }

    @Test
    public void testGoogleJwtCreatorNonStandardClaimDate() throws Exception {
        Algorithm algorithm = Algorithm.HMAC256("secret");
        String token = GoogleJwtCreator.build()
                .withPicture(PICTURE)
                .withEmail(EMAIL)
                .withIssuer("issuer")
                .withSubject("subject")
                .withAudience("audience")
                .withExp(exp)
                .withIat(iat)
                .withName(NAME)
                .withNonStandardClaim("nonStandardClaim", new Date())
                .sign(algorithm);
        GoogleVerification verification = GoogleJWT.require(algorithm);
        JWT verifier = verification.createVerifierForGoogle(PICTURE, EMAIL, asList("issuer"), asList("audience"),
                NAME, 1, 1).build();
        DecodedJWT jwt = verifier.decode(token);
        Map<String, Claim> claims = jwt.getClaims();
        verifyClaims(claims, exp);
    }

    @Test
    public void testGoogleJwtCreatorExpTimeHasPassed() throws Exception {
        thrown.expect(TokenExpiredException.class);
        thrown.expectMessage("The Token has expired on Wed Oct 29 00:00:00 PDT 2014.");

        String myDate = "2014/10/29";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date date = sdf.parse(myDate);
        long expLong = date.getTime();
        Date expDate = new Date(expLong);

        Algorithm algorithm = Algorithm.HMAC256("secret");
        String token = GoogleJwtCreator.build()
                .withPicture(PICTURE)
                .withEmail(EMAIL)
                .withIssuer("issuer")
                .withSubject("subject")
                .withAudience("audience")
                .withExp(expDate)
                .withIat(iat)
                .withName(NAME)
                .sign(algorithm);
        GoogleVerification verification = GoogleJWT.require(algorithm);
        JWT verifier = verification.createVerifierForGoogle(PICTURE, EMAIL, asList("issuer"), asList("audience"),
                NAME, 1, 1).build();
        DecodedJWT jwt = verifier.decode(token);
    }

    @Test
    public void testGoogleJwtCreatorTokenCantBeUsedBefore() throws Exception {
        thrown.expect(InvalidClaimException.class);
        thrown.expectMessage("The Token can't be used before Mon Oct 29 00:00:00 PDT 2018.");

        String myDate = "2018/10/29";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date date = sdf.parse(myDate);
        long expLong = date.getTime();
        Date iatDate = new Date(expLong);

        Algorithm algorithm = Algorithm.HMAC256("secret");
        String token = GoogleJwtCreator.build()
                .withPicture(PICTURE)
                .withEmail(EMAIL)
                .withIssuer("issuer")
                .withSubject("subject")
                .withAudience("audience")
                .withExp(exp)
                .withIat(iatDate)
                .withName(NAME)
                .sign(algorithm);
        GoogleVerification verification = GoogleJWT.require(algorithm);
        JWT verifier = verification.createVerifierForGoogle(PICTURE, EMAIL, asList("issuer"), asList("audience"),
                NAME, 1, 1).build();
        DecodedJWT jwt = verifier.decode(token);
    }

    @Test
    public void testCreateVerifierForExtended() throws Exception {
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("you shouldn't be calling this method");
        GoogleVerification verification = GoogleJWT.require(Algorithm.HMAC256("secret"));
        verification.createVerifierForExtended(null, null, null, null, null, 1L, 1L, 1L);
    }

    protected static void verifyClaims(Map<String, Claim> claims, Date exp) {
        assertTrue(claims.get(PICTURE).asString().equals(PICTURE));
        assertTrue(claims.get(EMAIL).asString().equals(EMAIL));
        assertTrue(claims.get(PublicClaims.ISSUER).asList(String.class).get(0).equals("issuer"));
        assertTrue(claims.get(PublicClaims.SUBJECT).asList(String.class).get(0).equals("subject"));
        assertTrue(claims.get(PublicClaims.AUDIENCE).asString().equals("audience"));
        assertTrue(claims.get(PublicClaims.EXPIRES_AT).asDate().toString().equals(exp.toString()));
        assertTrue(claims.get(NAME).asString().equals(NAME));
    }
}