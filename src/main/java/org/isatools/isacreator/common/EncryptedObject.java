/**
 ISAcreator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAcreator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 The contents of this file are subject to the CPAL version 1.0 (the License);
 you may not use this file except in compliance with the License. You may obtain a
 copy of the License at http://isa-tools.org/licenses/ISAcreator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections
 14 and 15 have been added to cover use of software over a computer network and
 provide for limited attribution for the Original Developer. In addition, Exhibit
 A has been modified to be consistent with Exhibit B.

 Software distributed under the License is distributed on an AS IS basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 the specific language governing rights and limitations under the License.

 The Original Code is ISAcreator.
 The Original Developer is the Initial Developer. The Initial Developer of the
 Original Code is the ISA Team (Eamonn Maguire, eamonnmag@gmail.com;
 Philippe Rocca-Serra, proccaserra@gmail.com; Susanna-Assunta Sansone, sa.sanson@gmail.com;
 http://www.isa-tools.org). All portions of the code written by the ISA Team are
 Copyright (c) 2007-2011 ISA Team. All Rights Reserved.

 EXHIBIT B. Attribution Information
 Attribution Copyright Notice: Copyright (c) 2008-2011 ISA Team
 Attribution Phrase: Developed by the ISA Team
 Attribution URL: http://www.isa-tools.org
 Graphic Image provided in the Covered Code as file: http://isa-tools.org/licenses/icons/poweredByISAtools.png
 Display of Attribution Information is required in Larger Works which are defined in the CPAL as a work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.

 Sponsors:
 The ISA Team and the ISA software suite have been funded by the EU Carcinogenomics project (http://www.carcinogenomics.eu), the UK BBSRC (http://www.bbsrc.ac.uk), the UK NERC-NEBC (http://nebc.nerc.ac.uk) and in part by the EU NuGO consortium (http://www.nugo.org/everyone).
 */


package org.isatools.isacreator.common;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

/**
 * EncryptedObject provides a way of encrypting and decrypting an object using Passphrases to generate a high strength key.
 * Encrypt Object encrypts and decrypts an object stream
 * Created with aid of knowledge and code gained from http://exampledepot.com/egs/javax.crypto/PassKey.html, last accessed 10-08-2008
 *
 * @author Eamonn Maguire
 */
public class EncryptedObject {
    private Cipher decryptCipher;
    private Cipher encryptCipher;

    // the salt is random bits which are used to derive the key. in combination with the passphrase, this provides
    // a key which tries to prevent against brute force attacks on the passphrase.
    private byte[] salt = {
            (byte) 0xB7, (byte) 0x9A, (byte) 0xC1, (byte) 0x65, (byte) 0x7B,
            (byte) 0xA5, (byte) 0xF2, (byte) 0x98,
    };

    // Iteration count
    int iterationCount = 1892;

    /**
     * Decrypts a SealedObject and returns the original Serialized Object.
     *
     * @param toDecrypt - The SealedObject to decrypt
     * @return Serialized object which was contained within the encrypted object
     * @throws BadPaddingException       //
     * @throws IOException               //
     * @throws IllegalBlockSizeException //
     * @throws ClassNotFoundException    //
     */
    public Serializable decryptObject(SealedObject toDecrypt)
            throws BadPaddingException, IOException, IllegalBlockSizeException,
            ClassNotFoundException {
        return (Serializable) toDecrypt.getObject(decryptCipher);
    }

    /**
     * Encrypts any serialized object as a SealedObject
     *
     * @param toEncrypt - The Serialized object to be encrypted
     * @return SealedObject which contains the encrypted version of the Serialized object supplied
     * @throws IOException               //
     * @throws IllegalBlockSizeException //
     */
    public SealedObject encryptObject(Serializable toEncrypt)
            throws IOException, IllegalBlockSizeException {
        return new SealedObject(toEncrypt, encryptCipher);
    }

    /**
     * Generates a key given a passphrase using the PBEWithMD5AndDES algorithm
     *
     * @param passphrase - The passphrase to generate the key with
     * @throws NoSuchAlgorithmException *
     * @throws NoSuchPaddingException   *
     * @throws InvalidKeyException      *
     * @throws InvalidKeySpecException  *
     * @throws InvalidAlgorithmParameterException
     *                                  *
     */
    public void generateKey(String passphrase)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidKeySpecException,
            InvalidAlgorithmParameterException {
        KeySpec keySpec = new PBEKeySpec(passphrase.toCharArray(), salt,
                iterationCount);

        SecretKey secretKey = SecretKeyFactory.getInstance("PBEWithMD5AndDES")
                .generateSecret(keySpec);

        // initialise the encryption cipher
        encryptCipher = Cipher.getInstance(secretKey.getAlgorithm());

        // initialise the decryption cipher
        decryptCipher = Cipher.getInstance(secretKey.getAlgorithm());

        AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt,
                iterationCount);

        encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey, paramSpec);
        decryptCipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);
    }
}
