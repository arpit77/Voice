package com.example.voice;

public class AudioStatus {

    int audioState;
    int currentValue;
    int totalDuration;

    public AudioStatus(int audioState, int currentValue) {
        this.audioState = audioState;
        this.currentValue = currentValue;
    }

    public int getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(int totalDuration) {
        this.totalDuration = totalDuration;
    }

    public int getAudioState() {
        return audioState;
    }

    public void setAudioState(int audioState) {
        this.audioState = audioState;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
    }

    public enum AUDIO_STATE {
        IDLE,
        PLAYING,
        PAUSED
    }

}
