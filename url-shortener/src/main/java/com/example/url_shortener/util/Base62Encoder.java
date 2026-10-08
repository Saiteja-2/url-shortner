package com.example.url_shortener.util;

public class Base62Encoder {
        private static final String CHARACTERS =
                "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

        public static String encode(long number) {

            if (number == 0) {
                return "0";
            }

            StringBuilder result = new StringBuilder();

            while (number > 0) {

                int remainder = (int) (number % 62);

                result.append(CHARACTERS.charAt(remainder));

                number = number / 62;
            }

            return result.reverse().toString();
        }
    public static void main(String[] args) {

        System.out.println(Base62Encoder.encode(1));
        System.out.println(Base62Encoder.encode(10));
        System.out.println(Base62Encoder.encode(100));
        System.out.println(Base62Encoder.encode(1000));
        System.out.println(Base62Encoder.encode(10000));
    }
}

