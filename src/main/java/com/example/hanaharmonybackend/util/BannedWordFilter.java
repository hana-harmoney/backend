package com.example.hanaharmonybackend.util;

import java.util.List;
import java.util.regex.Pattern;

public class BannedWordFilter {

    private static final List<String> BANNED_WORDS = List.of(
            "링크결제", "수수료", "돈 좀 빌려줘", "급하게 필요해", "급전", "안전결제",
            "보상", "원금 보장", "믿고 투자", "투자 보장", "대출", "수술비", "입원비", "처방전",

            "주민등록번호", "신분증", "인증번호",
            "OTP", "카드번호", "보안카드", "공인인증서",
            "비밀번호", "메일주소",

            "통신이 안돼", "믿어줘", "내 말 안 믿어?", "믿지 않으면 넌 실망이야",
            "신고할 거야?", "더 이상 연락 안 해",

            "씨발", "시발", "ㅅㅂ", "ㅂㅅ", "ㄲㅈ",
            "병신", "지랄", "개새", "개새끼",
            "꺼져", "저능아", "찐따", "미친놈", "미친새끼",
            "또라이", "븅", "븅신", "시발놈",
            "개노답", "노답", "빡쳐", "엿먹어",
            "죽여버려", "뒤질래", "존나", "ㅈㄴ", "니애미",
            "노친네", "고려장", "틀딱"
    );

    public static String maskBannedWords(String message) {
        String filteredMessage = message;
        for (String banned : BANNED_WORDS) {
            filteredMessage = filteredMessage.replaceAll("(?i)" + Pattern.quote(banned), "*".repeat(banned.length()));
        }
        return filteredMessage;
    }
}
