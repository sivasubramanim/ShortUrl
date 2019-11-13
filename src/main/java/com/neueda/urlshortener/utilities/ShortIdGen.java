package com.neueda.urlshortener.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShortIdGen {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShortIdGen.class);
    private static final Integer ToBase = 62;
    public static String createUniqueID(Long id) {
         return convertBase10toAnyBase(ToBase,id);
    }
    public static long getDictionaryKeyFromUniqueID(String uniqueID) {
        return convertToBase10FromAnyBase(uniqueID,ToBase);
    }
   private static String convertBase10toAnyBase(int base1, long inputNum)
    {
        String s = "";
        while (inputNum > 0)
        {
            s += reVal(inputNum % base1);
            inputNum /= base1;
        }
        StringBuilder ix = new StringBuilder();
        ix.append(s);
        return new String(ix.reverse());
    }
    private static long convertToBase10FromAnyBase(String str, int base)
    {
        int len = str.length();
        int power = 1;
        long num = 0L;
        int i;
        for (i = len - 1; i >= 0; i--)
        {
            if (val(str.charAt(i)) >= base)
            {
                LOGGER.error("[convertToBase10FromAnyBase]: Invalid Number: {}" , str);
                return -1;
            }
            num += val(str.charAt(i)) * power;
            power = power * base;
        }

        return num;
    }
    private static int val(char c)
    {
        if (c >= '0' && c <= '9')
            return (int)c - '0';
        else
            return (int)c - 'A' + 10;
    }
    private static char reVal(long num)
    {
        if (num >= 0 && num <= 9)
            return (char)(num + 48);
        else
            return (char)(num - 10 + 65);
    }

}
