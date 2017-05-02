package net.gluu.erasmus.model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ParticipantsResponse {

    @SerializedName("participants")
    @Expose
    private List<Participant> participants = null;
    @SerializedName("error")
    @Expose
    private Boolean error;

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public ParticipantsResponse withParticipants(List<Participant> participants) {
        this.participants = participants;
        return this;
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ParticipantsResponse withError(Boolean error) {
        this.error = error;
        return this;
    }

}
