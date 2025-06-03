package startwithco.tosssnapshot.exception.code;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExceptionCodeMapper {
    private static final Map<String, String> BAD_REQUEST_MAP = new ConcurrentHashMap<>();
    private static final Map<String, String> CONFLICT_MAP = new ConcurrentHashMap<>();
    private static final Map<String, String> NOT_FOUND_MAP = new ConcurrentHashMap<>();
    private static final Map<String, String> SERVER_MAP = new ConcurrentHashMap<>();
    private static final Map<String, String> UNAUTHORIZED_MAP = new ConcurrentHashMap<>();

    static {
        // BadRequestException
        BAD_REQUEST_MAP.put("요청 데이터 오류입니다.", "BAD_REQUEST_EXCEPTION_001");
        BAD_REQUEST_MAP.put("결제 금액이 TOSS PAYMENT 승인 금액과 다릅니다.", "BAD_REQUEST_EXCEPTION_003");
        BAD_REQUEST_MAP.put("인증코드가 일치하지 않습니다.", "BAD_REQUEST_EXCEPTION_006");
        BAD_REQUEST_MAP.put("비밀번호가 일치하지 않습니다.", "BAD_REQUEST_EXCEPTION_007");
        BAD_REQUEST_MAP.put("해당 결제 요청은 승인할 수 없습니다. 결제 승인 진행 중입니다.", "BAD_REQUEST_EXCEPTION_008");
        BAD_REQUEST_MAP.put("지원하지 않는 결제 수단입니다.", "BAD_REQUEST_EXCEPTION_009");
        BAD_REQUEST_MAP.put("웹훅 서명 검증에 실패했습니다.", "BAD_REQUEST_EXCEPTION_010");

        // ConflictException
        CONFLICT_MAP.put("중복된 이메일입니다.", "CONFLICT_EXCEPTION_001");
        CONFLICT_MAP.put("동시성 저장은 불가능합니다.", "CONFLICT_EXCEPTION_002");
        CONFLICT_MAP.put("해당 벤더의 해당 카테고리 솔루션이 이미 존재합니다.", "CONFLICT_EXCEPTION_004");
        CONFLICT_MAP.put("같은 솔루션에 리뷰는 한 번만 작성할 수 있습니다.", "CONFLICT_EXCEPTION_005");

        // NotFoundException
        NOT_FOUND_MAP.put("존재하지 않는 벤더 기업입니다.", "NOT_FOUND_EXCEPTION_001");
        NOT_FOUND_MAP.put("존재하지 않는 결제 요청입니다.", "NOT_FOUND_EXCEPTION_002");
        NOT_FOUND_MAP.put("존재하지 않는 결제입니다.", "NOT_FOUND_EXCEPTION_003");
        NOT_FOUND_MAP.put("존재하지 않는 수요 기업입니다.", "NOT_FOUND_EXCEPTION_004");
        NOT_FOUND_MAP.put("존재하지 않는 솔루션입니다.", "NOT_FOUND_EXCEPTION_005");
        NOT_FOUND_MAP.put("존재하지 않는 코드입니다.", "NOT_FOUND_EXCEPTION_006");
        NOT_FOUND_MAP.put("수요 기업이 해당 솔루션에 작성한 리뷰가 없습니다.", "NOT_FOUND_EXCEPTION_007");
        NOT_FOUND_MAP.put("해당 기업이 작성한 카테고리 솔루션이 존재하지 않습니다.", "NOT_FOUND_EXCEPTION_008");
        NOT_FOUND_MAP.put("존재하지 않는 이메일 입니다.", "NOT_FOUND_EXCEPTION_009");

        // ServerException
        SERVER_MAP.put("내부 서버 오류가 발생했습니다.", "SERVER_EXCEPTION_001");
        SERVER_MAP.put("S3 UPLOAD 실패", "SERVER_EXCEPTION_002");
        SERVER_MAP.put("결제 응답 파싱 중 오류가 발생했습니다.", "SERVER_EXCEPTION_003");
        SERVER_MAP.put("구매 확정, 정산 완료 결제 요청이지만 결제 승인된 정보가 없습니다.", "SERVER_EXCEPTION_004");
        SERVER_MAP.put("중복된 결제 데이터가 존재합니다.", "SERVER_EXCEPTION_005");
        SERVER_MAP.put("토스페이먼츠 결제 승인 실패", "SERVER_EXCEPTION_006");
        SERVER_MAP.put("WebClient 응답 에러가 발생했습니다.", "SERVER_EXCEPTION_007");
        SERVER_MAP.put("무통장 입금 전 결제가 저장되지 않았습니다.", "SERVER_EXCEPTION_008");
        SERVER_MAP.put("환불이 불가능한 결제입니다.", "SERVER_EXCEPTION_009");
        SERVER_MAP.put("웹훅 처리 중 서버 오류가 발생했습니다.", "SERVER_EXCEPTION_010");

        // UnauthorizedException
    }

    public static String getCode(String message, ExceptionType type) {
        return switch (type) {
            case BAD_REQUEST -> BAD_REQUEST_MAP.getOrDefault(message, "BAD_REQUEST_EXCEPTION_예외코드 설정하세요.");
            case CONFLICT -> CONFLICT_MAP.getOrDefault(message, "CONFLICT_EXCEPTION_예외코드 설정하세요.");
            case NOT_FOUND -> NOT_FOUND_MAP.getOrDefault(message, "NOT_FOUNE_EXCEPTION_예외코드 설정하세요.");
            case SERVER -> SERVER_MAP.getOrDefault(message, "SERVER_EXCEPTION_예외코드 설정하세요.");
            case UNAUTHORIZED -> UNAUTHORIZED_MAP.getOrDefault(message, "UNAUTHORIZED_EXCEPTION_예외코드 설정하세요.");
        };
    }

    public enum ExceptionType {
        BAD_REQUEST,
        CONFLICT,
        NOT_FOUND,
        SERVER,
        UNAUTHORIZED
    }
}
