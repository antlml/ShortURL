package com.utils;

import java.util.Stack;

/**
 * 转换工具类
 */
public class PECode {
    public static final char[] array={'q','w','e','r','t','y','u','i','o','p','a','s','d','f','g','h','j','k','l','z','x','c','v','b','n','m','0','1','2','3','4','5','6','7','8','9','Q','W','E','R','T','Y','U','I','O','P','A','S','D','F','G','H','J','K','L','Z','X','C','V','B','N','M'};

    /**
     * 10进制转62进制
     * @param number 10进制
     * @return String 32进制
     */
    public static String _10_to_62(int number){
        Integer rest=number;
        Stack<Character> stack=new Stack<Character>();
        StringBuilder result=new StringBuilder(0);
        while(rest!=0){
            stack.add(array[new Long((rest-(rest/62)*62)).intValue()]);
            rest=rest/62;
        }
        for(;!stack.isEmpty();){
            result.append(stack.pop());
        }
        return result.toString();

    }

    /**
     * 62进制转10进制
     * @param sixty_str 62进制
     * @return int 10进制
     */
    public static int _62_to_10(String sixty_str){
        int multiple=1;
        int result=0;
        Character c;
        for(int i=0;i<sixty_str.length();i++){
            c=sixty_str.charAt(sixty_str.length()-i-1);
            result+=_62_value(c)*multiple;
            multiple=multiple*62;
        }

        return result;
    }

    private static int _62_value(Character c){
        for(int i=0;i<array.length;i++){
            if(c==array[i]){
                return i;
            }
        }
        return -1;
    }
}
