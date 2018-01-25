package com.auth0.jwt.oicmsg;

import com.auth0.jwt.exceptions.oicmsg_exceptions.HeaderError;
import com.google.common.base.Strings;
import com.google.common.primitives.Bytes;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.util.encoders.Base64;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Key {

    final private static Logger logger = LoggerFactory.getLogger(Key.class);
    protected String kty;
    protected String alg;
    protected String use;
    protected String kid;
    protected String x5c;
    protected String x5t;
    protected String x5u;
    protected Key key;
    protected long inactiveSince;
    protected Map<String, String> args;
    private static final String SHA_256 = "SHA-256";
    private static final String SHA_384 = "SHA-384";
    private static final String SHA_512 = "SHA-512";
    private static Map<String, Object> longs = new HashMap<String, Object>();
    protected static Set<String> members = new HashSet<>(Arrays.asList("kty", "alg", "use", "kid", "x5c", "x5t", "x5u"));
    public static Set<String> publicMembers = new HashSet<>(Arrays.asList("kty", "alg", "use", "kid", "x5c", "x5t", "x5u"));
    protected static List<String> required = Arrays.asList("kty");
    private static final List<String> signs = Arrays.asList("+", "/", "=");

    public Key(String kty, String alg, String use, String kid, String x5c, String x5t, String x5u, Key key, Map<String, String> args) {
        this.kty = kty;
        this.alg = alg;
        this.use = use;
        this.kid = kid;
        this.x5c = x5c;
        this.x5t = x5t;
        this.x5u = x5u;
        this.inactiveSince = 0;
        this.key = key;
        this.args = args;
    }

    public Key() {
        this("", "", "", "", "", "", "", null, null);
    }

    public String getX5c() {
        return x5c;
    }

    public void setX5c(String x5c) {
        this.x5c = x5c;
    }

    public String getX5t() {
        return x5t;
    }

    public void setX5t(String x5t) {
        this.x5t = x5t;
    }

    public String getX5u() {
        return x5u;
    }

    public void setX5u(String x5u) {
        this.x5u = x5u;
    }

    public String getKty() {
        return kty;
    }

    public void setKty(String kty) {
        this.kty = kty;
    }

    public String getAlg() {
        return alg;
    }

    public void setAlg(String alg) {
        this.alg = alg;
    }

    public String getUse() {
        return use;
    }

    public void setUse(String use) {
        this.use = use;
    }

    public String getKid() {
        return kid;
    }

    public void setKid(String kid) {
        this.kid = kid;
    }

    public void setInactiveSince() {
        this.inactiveSince = System.currentTimeMillis();
    }

    public long getInactiveSince() {
        return inactiveSince;
    }

    public Map<String, String> toDict() {
        Map<String, String> hmap = serialize();
        for (String key : args.keySet()) {
            hmap.put(key, args.get(key));
        }
        return hmap;
    }

    public Key serialize() {
        Map<String, String> hmap = common();
        this.key.
        //TODO
    }

    public Map<String, String> common() {
        Map<String, String> args = new HashMap<>();
        args.put("kty", this.kty);
        if (!Strings.isNullOrEmpty(this.use)) {
            args.put("use", this.use);
        }
        if (!Strings.isNullOrEmpty(this.kid)) {
            args.put("kid", this.kid);
        }
        if (!Strings.isNullOrEmpty(this.alg)) {
            args.put("alg", this.alg);
        }
        return args;
    }

    @Override
    public String toString() {
        return this.toDict().toString();
    }

    public boolean verify() throws HeaderError {
        Object item = null;
        for (String key : longs.keySet()) {

            try {
                item = this.getClass().getField(key).get(this);
            } catch (Exception e1) {
                logger.error("Field " + key + " doesn't exist");
            }
            if (item == null || item instanceof Number) {
                continue;
            }

            if (item instanceof Bytes) {

                //item = item.decode('utf-8') ???
                //TODO
            }

            try {
                base64URLToLong(item);
            } catch (Exception e) {
                return false;
            }
            for (String sign : signs) {
                if (((String) item).contains(sign)) {
                    return false;
                }
            }

            if (!Strings.isNullOrEmpty(this.kid)) {
                try {
                    Assert.assertTrue(this.kid instanceof String);
                } catch (AssertionError error) {
                    throw new HeaderError("kid of wrong value type");
                }
            }
        }

        return true;
    }

    private void base64URLToLong(Object item) {

    }

    @Override
    public boolean equals(Object other) {
        try {
            Assert.assertTrue(other instanceof Key);
            //Assert.assertTrue(); //TODO
            Key otherKey = (Key) other;
            Assert.assertEquals(this.getKty(), otherKey.kty);
            Assert.assertEquals(this.getAlg(), otherKey.alg);
            Assert.assertEquals(this.getUse(), otherKey.use);
            Assert.assertEquals(this.getKid(), otherKey.kid);
            Assert.assertEquals(this.getX5c(), otherKey.x5c);
            Assert.assertEquals(this.getX5t(), otherKey.x5t);
            Assert.assertEquals(this.getX5u(), otherKey.x5u);
        } catch (AssertionError error) {
            return false;
        }
        return true;
    }

    public List<String> getKeys() {
        return new ArrayList<>(this.toDict().keySet());
    }

    public byte[] thumbprint(String hashFunction, List<String> members) throws NoSuchFieldException {
        if (members == null || members.isEmpty()) {
            members = required;
        }

        Collections.sort(members);
        Key key = this.serialize();
        String value = null;
        Map<String, String> hmap = new HashMap<>();
        for (String member : members) {
            value = key.getClass().getField(member).toString();
            hmap.put(member, value);
        }

        String json = new Gson().toJson(hmap);
        byte[] byteArr = null;
        switch (hashFunction) {
            case SHA_256:
                byteArr = sha256_digest(json);
                break;
            case SHA_384:
                byteArr = sha384_digest(json);
                break;
            case SHA_512:
                byteArr = sha512_digest(json);
                break;
            default:
                throw new IllegalArgumentException("improper hash function");
        }

        return byteArr;
    }

    public byte[] thumbprint(String hashFunction) {
        thumbprint(hashFunction, null);
    }

    public void addKid() {
        this.kid = new String(Base64.encode(this.thumbprint("SHA-256")));
    }


    //https://stackoverflow.com/questions/5729806/encode-string-to-utf-8 can't encode string to utf-8
    /*protected static void deser(Object item) {
        if(item instanceof String) {
            item.en
        }



        return base64ToLong(item);
    }*/
}
