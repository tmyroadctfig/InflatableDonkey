/*
 * The MIT License
 *
 * Copyright 2016 Ahseya.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.horrorho.inflatabledonkey.data.backup;

import java.util.Base64;
import java.util.Optional;
import net.jcip.annotations.Immutable;
import org.bouncycastle.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Ahseya
 */
@Immutable
public final class KeyBagID {

    private static final Logger logger = LoggerFactory.getLogger(KeyBagID.class);

    public static Optional<KeyBagID> from(String string) {
        Optional<KeyBagID> keyBagID = doFrom(string);
        keyBagID.filter(u -> !u.toString().equals(string))
                .ifPresent(u -> {
                    logger.warn("-- from() - mismatch in: {} out: {}", string, u.toString());
                });
        return keyBagID;
    }

    static Optional<KeyBagID> doFrom(String id) {
        // Format: K:<base64 uuid>
        String[] split = id.split(":");
        if (split.length != 2 || !split[0].equals("K")) {
            logger.warn("-- doFrom() - bad format: {}", id);
            return Optional.empty();
        }
        return decode(split[1]).map(KeyBagID::new);
    }

    static Optional<byte[]> decode(String base64) {
        try {
            return Optional.of(Base64.getDecoder().decode(base64));
        } catch (IllegalArgumentException ex) {
            logger.warn("-- decode() - IllegalArgumentException: {}", ex.getMessage());
            return Optional.empty();
        }
    }

    private final byte[] uuid;
    private final String uuidBase64;

    public KeyBagID(byte[] uuid) {
        this.uuid = Arrays.copyOf(uuid, uuid.length);
        uuidBase64 = Base64.getEncoder().encodeToString(uuid);
    }

    public byte[] uuid() {
        return Arrays.copyOf(uuid, uuid.length);
    }

    public String uuidBase64() {
        return uuidBase64;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + java.util.Arrays.hashCode(this.uuid);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final KeyBagID other = (KeyBagID) obj;
        if (!java.util.Arrays.equals(this.uuid, other.uuid)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "K:" + uuidBase64;
    }
}
