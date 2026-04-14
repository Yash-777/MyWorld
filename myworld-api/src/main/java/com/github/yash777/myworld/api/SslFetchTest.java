package com.github.yash777.myworld.api;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.util.*;

/**
 * SslFetchTest — Standalone Java 8, zero dependencies.
 *
 * Compile:  javac SslFetchTest.java
 * Run:      java SslFetchTest
 *
 * Steps:
 *   1. Load JVM cacerts as base truststore
 *   2. Open trust-all socket → capture ZScaler cert chain
 *   3. Import chain into in-memory KeyStore
 *   4. Install patched KeyStore as JVM-wide SSLContext
 *   5. Fetch target URL with HttpsURLConnection → print result
 */
public class SslFetchTest {

    // ── Change this to any URL you want to test ──────────────────────────────
    private static final String TARGET_URL =
        "https://azuredownloads-g3ahgwb5b8bkbxhd.b01.azurefd.net/github-copilot/content.xml";

    private static final int TIMEOUT_MS = 15_000;

    public static void main(String[] args) throws Exception {

        System.out.println("============================================================");
        System.out.println("  Java     : " + System.getProperty("java.version"));
        System.out.println("  OS       : " + System.getProperty("os.name"));
        System.out.println("  JAVA_HOME: " + System.getProperty("java.home"));
        System.out.println("  Target   : " + TARGET_URL);
        System.out.println("============================================================");

        // ── PHASE 1: What does vanilla Java see? (before any fix) ────────────
        System.out.println("\n[PHASE 1] Raw fetch — expect PKIX error if ZScaler is active:");
        rawFetch(TARGET_URL);

        // ── PHASE 2: Capture ZScaler cert chain ──────────────────────────────
        String host = extractHost(TARGET_URL);
        System.out.println("\n[PHASE 2] Capturing cert chain from " + host + ":443 ...");
        List<X509Certificate> chain = captureChain(host, 443);

        if (chain.isEmpty()) {
            System.out.println("[ERROR] No certs captured — host unreachable. Check network.");
            return;
        }

        // ── PHASE 3: Import chain into JVM truststore ─────────────────────────
        System.out.println("\n[PHASE 3] Importing certs into in-memory truststore ...");
        KeyStore ts = loadCacerts();
        for (int i = 0; i < chain.size(); i++) {
            String alias = host.replace(".", "-") + "-" + i;
            if (!ts.containsAlias(alias)) {
                ts.setCertificateEntry(alias, chain.get(i));
                System.out.println("  Imported alias: " + alias);
            } else {
                System.out.println("  Already exists: " + alias);
            }
        }

        // ── PHASE 4: Patch JVM-wide SSLContext ────────────────────────────────
        System.out.println("\n[PHASE 4] Patching JVM SSLContext (" + ts.size() + " entries) ...");
        TrustManagerFactory tmf =
            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ts);
        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(null, tmf.getTrustManagers(), new SecureRandom());
        SSLContext.setDefault(ctx);
        HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
        System.out.println("  Done — SSLContext.setDefault() + setDefaultSSLSocketFactory()");

        // ── PHASE 5: Retry fetch with patched truststore ──────────────────────
        System.out.println("\n[PHASE 5] Re-fetching with patched truststore:");
        patchedFetch(TARGET_URL);

        System.out.println("\n============================================================");
        System.out.println("  Done.");
        System.out.println("============================================================");
    }

    // ── Phase 1: raw fetch to reproduce the PKIX error ───────────────────────

    private static void rawFetch(String urlStr) {
        try {
            HttpsURLConnection conn = openConn(urlStr);
            int status = conn.getResponseCode();
            System.out.println("  HTTP " + status + " — no PKIX error (ZScaler already trusted by JVM)");
            conn.disconnect();
        } catch (SSLHandshakeException e) {
            System.out.println("  SSLHandshakeException (PKIX confirmed): " + e.getMessage());
        } catch (Exception e) {
            System.out.println("  " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    // ── Phase 2: trust-all socket to capture the cert chain ──────────────────

    private static List<X509Certificate> captureChain(String host, int port) {
        List<X509Certificate> chain = new ArrayList<X509Certificate>();
        try {
            final X509Certificate[][] holder = new X509Certificate[1][];

            SSLContext trustAll = SSLContext.getInstance("TLS");
            trustAll.init(null, new TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] c, String a) {}
                public void checkServerTrusted(X509Certificate[] c, String a) { holder[0] = c; }
                public X509Certificate[] getAcceptedIssuers() {
                    return holder[0] != null ? holder[0] : new X509Certificate[0];
                }
            }}, new SecureRandom());

            SSLSocket sock = (SSLSocket) trustAll.getSocketFactory().createSocket(host, port);
            try {
                sock.setSoTimeout(TIMEOUT_MS);
                sock.startHandshake();
                for (Certificate c : sock.getSession().getPeerCertificates()) {
                    if (c instanceof X509Certificate) {
                        X509Certificate x = (X509Certificate) c;
                        chain.add(x);
                        System.out.println("  cert[" + (chain.size()-1) + "]"
                            + "\n    Subject : " + x.getSubjectDN()
                            + "\n    Issuer  : " + x.getIssuerDN()
                            + "\n    Expires : " + x.getNotAfter()
                            + "\n    Serial  : " + x.getSerialNumber().toString(16));
                    }
                }
            } finally {
                try { sock.close(); } catch (Exception ignored) {}
            }
        } catch (Exception e) {
            System.out.println("  [ERROR] captureChain: " + e.getMessage());
        }
        return chain;
    }

    // ── Load JVM cacerts as base ──────────────────────────────────────────────

    private static KeyStore loadCacerts() throws Exception {
        String home = System.getProperty("java.home");
        File f = new File(home, "lib/security/cacerts");          // Java 11+
        if (!f.exists()) f = new File(home, "jre/lib/security/cacerts"); // Java 8
        System.out.println("  cacerts: " + f.getAbsolutePath());
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        FileInputStream fis = new FileInputStream(f);
        try { ks.load(fis, "changeit".toCharArray()); } finally { fis.close(); }
        System.out.println("  Loaded " + ks.size() + " base entries");
        return ks;
    }

    // ── Phase 5: fetch with patched truststore ────────────────────────────────

    private static void patchedFetch(String urlStr) {
        try {
            HttpsURLConnection conn = openConn(urlStr);
            int status = conn.getResponseCode();
            System.out.println("  HTTP Status  : " + status + " " + conn.getResponseMessage());
            System.out.println("  Content-Type : " + conn.getContentType());
            System.out.println("  Content-Len  : " + conn.getContentLength());

            InputStream is = (status >= 200 && status < 300)
                ? conn.getInputStream() : conn.getErrorStream();

            if (is != null) {
                BufferedReader br =
                    new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                String line;
                int chars = 0;
                while ((line = br.readLine()) != null && chars < 1000) {
                    sb.append(line).append("\n");
                    chars += line.length();
                }
                br.close();
                System.out.println("\n  Body (first 1000 chars):");
                System.out.println("  ┌───────────────────────────────────────────────────────");
                for (String l : sb.toString().split("\n")) {
                    System.out.println("  │ " + l);
                }
                System.out.println("  └───────────────────────────────────────────────────────");
            }
            conn.disconnect();

        } catch (SSLHandshakeException e) {
            System.out.println("  [STILL FAILING] SSLHandshakeException: " + e.getMessage());
            System.out.println("  → The cert ZScaler presented was NOT in the captured chain.");
            System.out.println("  → Re-run with: java -Djavax.net.debug=ssl:handshake SslFetchTest");
        } catch (Exception e) {
            System.out.println("  [ERROR] " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static HttpsURLConnection openConn(String urlStr) throws Exception {
        HttpsURLConnection c = (HttpsURLConnection) new URL(urlStr).openConnection();
        c.setConnectTimeout(TIMEOUT_MS);
        c.setReadTimeout(TIMEOUT_MS);
        c.setRequestMethod("GET");
        c.setRequestProperty("User-Agent", "Java/" + System.getProperty("java.version"));
        c.setRequestProperty("Accept", "*/*");
        return c;
    }

    private static String extractHost(String url) {
        String s = url;
        if (s.startsWith("https://")) s = s.substring(8);
        if (s.startsWith("http://"))  s = s.substring(7);
        int slash = s.indexOf('/');
        if (slash != -1) s = s.substring(0, slash);
        int colon = s.indexOf(':');
        if (colon != -1) s = s.substring(0, colon);
        return s.trim();
    }
}