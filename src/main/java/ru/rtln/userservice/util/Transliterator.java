package ru.rtln.userservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Transliterator {

    public static String cyrillicToLatin(String s) {
        StringBuilder sb = new StringBuilder(s.length() * 2);
        for (char ch : s.toCharArray()) {
            sb.append(cyrillicToLatin(Character.toLowerCase(ch)));
        }

        return sb.toString().toUpperCase();
    }

    private static String cyrillicToLatin(char c) {
        if (c >= 'a' && c <= 'z' || c >= '0' && c <= '9') {
            return String.valueOf(c);
        }

        return switch (c) {
            case 'а' -> "a";
            case 'б' -> "b";
            case 'в' -> "v";
            case 'г' -> "g";
            case 'д' -> "d";
            case 'е' -> "e";
            case 'ё' -> "je";
            case 'ж' -> "zh";
            case 'з' -> "z";
            case 'и' -> "i";
            case 'й' -> "y";
            case 'к' -> "k";
            case 'л' -> "l";
            case 'м' -> "m";
            case 'н' -> "n";
            case 'о' -> "o";
            case 'п' -> "p";
            case 'р' -> "r";
            case 'с' -> "s";
            case 'т' -> "t";
            case 'у' -> "u";
            case 'ф' -> "f";
            case 'х' -> "kh";
            case 'ц' -> "c";
            case 'ч' -> "ch";
            case 'ш' -> "sh";
            case 'щ' -> "jsh";
            case 'ъ' -> "hh";
            case 'ы' -> "ih";
            case 'ь' -> "jh";
            case 'э' -> "eh";
            case 'ю' -> "ju";
            case 'я' -> "ja";
            case ' ' -> "_";
            default -> "";
        };
    }
}