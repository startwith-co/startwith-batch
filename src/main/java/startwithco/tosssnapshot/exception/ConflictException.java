package startwithco.tosssnapshot.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ConflictException extends RuntimeException {
	private final int httpStatus;
	private final String message;
	private final String code;
}
