package lk.shan.firstmy.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Data
public class ResponseDto {
    private String message;
    private String code;
    private Object content;
}
