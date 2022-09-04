package hello.jdbc.domain;

import lombok.Data;

@Data
public class Member {

    private String memberId;
    private int money;

    // command + N >> 생성자 만드는 단축키
    public Member() {
    }

    public Member(String memberId, int money) {
        this.memberId = memberId;
        this.money = money;
    }


}
