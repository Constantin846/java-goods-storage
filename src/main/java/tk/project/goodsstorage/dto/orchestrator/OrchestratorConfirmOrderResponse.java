package tk.project.goodsstorage.dto.orchestrator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class OrchestratorConfirmOrderResponse {

    private final UUID businessKey;

    @JsonCreator
    public OrchestratorConfirmOrderResponse(@JsonProperty("businessKey") final UUID businessKey) {
        this.businessKey = businessKey;
    }
}
