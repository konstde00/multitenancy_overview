package com.konstde00.admin_module.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

@Slf4j
@UtilityClass
public class PasswordUtil {

    static CharacterRule characterRule = new CharacterRule(new CharacterData() {
        public String getErrorCode() {
            return "ERROR_CODE";
        }

        public String getCharacters() {
            return EnglishCharacterData.LowerCase.getCharacters()
                + EnglishCharacterData.UpperCase.getCharacters()
                + EnglishCharacterData.Alphabetical.getCharacters()
                + EnglishCharacterData.Special.getCharacters()
                + EnglishCharacterData.Digit.getCharacters();
        }
    });

    public static String generatePassword(Integer length) {

        PasswordGenerator passwordGenerator = new PasswordGenerator();

        return passwordGenerator.generatePassword(length, characterRule);
    }
}
