package hello.typeconverter.type;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * EqualsAndHashCode
 -> 모든 필드를 사용해서 equals(), hashcode() 를 생성
 -> 모든 필드의 값이 같다면 a.equals(b) 의 결과가 참

 * 참고
 - equals : 두 객체의 내용이 같은지, 동등성(equality) 를 비교하는 연산자
 - hashCode : 두 객체가 같은 객체인지, 동일성(identity) 를 비교하는 연산자

 - hash 값을 사용하는 Collection(HashMap, HashSet, HashTable)은 객체가 논리적으로 같은지 비교할 때
 - hashCode 메서드의 리턴 값이 우선 일치하고 equals 메서드의 리턴 값이 true 여야 논리적으로 같은 객체라고 판단
 */
@Getter
@EqualsAndHashCode
public class IpPort {
    private String ip;
    private int port;

    public IpPort(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }
}
