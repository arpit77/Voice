package com.example.voice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.ViewHolder> {

    private Context context;
    private List<RoomModel> voiceAudioList = new ArrayList<>();
    private MainActivity mainActivity;

    public AudioListAdapter(Context context, List<RoomModel> voiceAudioList) {
        this.context = context;
        this.voiceAudioList = voiceAudioList;
        this.mainActivity = (MainActivity) context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_layout_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String songPath = voiceAudioList.get(position).getPath();
//        String songName = songPath.substring(songPath.lastIndexOf("/") + 1);

        System.out.println("AudioListAdapter.onBindViewHolder" + voiceAudioList.size());
        if(mainActivity.audioStatusList.get(position).getAudioState() != AudioStatus.AUDIO_STATE.IDLE.ordinal()) {
            holder.seekBarAudio.setMax(mainActivity.audioStatusList.get(position).getTotalDuration());
            holder.seekBarAudio.setProgress(mainActivity.audioStatusList.get(position).getCurrentValue());
            holder.seekBarAudio.setEnabled(true);
        } else {
            holder.seekBarAudio.setProgress(0);
            holder.seekBarAudio.setEnabled(false);
        }

        if(mainActivity.audioStatusList.get(position).getAudioState() == AudioStatus.AUDIO_STATE.IDLE.ordinal()
                || mainActivity.audioStatusList.get(position).getAudioState() == AudioStatus.AUDIO_STATE.PAUSED.ordinal()) {
            holder.btnPlay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24));;
        } else {
            holder.btnPlay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_pause_24));;
        }
    }

    @Override
    public int getItemCount() {
        return voiceAudioList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView btnPlay;
        SeekBar seekBarAudio;



        ViewHolder(View itemView) {
            super(itemView);
//            ButterKnife.bind(this, itemView);
            btnPlay = itemView.findViewById(R.id.btnPlay);
            seekBarAudio = itemView.findViewById(R.id.seekBarAudio);

            seekBarAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(fromUser) MediaPlayerUtils.applySeekBarValue(progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    boolean ifRequest = mainActivity.requestPermissionIfNeeded();
//                    if(ifRequest) return;

                    int position = getAdapterPosition();

                    // Check if any other audio is playing
                    if(mainActivity.audioStatusList.get(position).getAudioState()
                            == AudioStatus.AUDIO_STATE.IDLE.ordinal()) {

                        // Reset media player
                        MediaPlayerUtils.Listener listener = (MediaPlayerUtils.Listener) context;
                        listener.onAudioComplete();
                    }

                    String audioPath = voiceAudioList.get(position).getPath();
                    AudioStatus audioStatus = mainActivity.audioStatusList.get(position);
                    int currentAudioState = audioStatus.getAudioState();

                    if(currentAudioState == AudioStatus.AUDIO_STATE.PLAYING.ordinal()) {
                        // If mediaPlayer is playing, pause mediaPlayer
                        btnPlay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24));
                        MediaPlayerUtils.pauseMediaPlayer();

                        audioStatus.setAudioState(AudioStatus.AUDIO_STATE.PAUSED.ordinal());
                        mainActivity.audioStatusList.set(position, audioStatus);
                    } else if(currentAudioState == AudioStatus.AUDIO_STATE.PAUSED.ordinal()) {
                        // If mediaPlayer is paused, play mediaPlayer
                        btnPlay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_pause_24));
                        MediaPlayerUtils.playMediaPlayer();

                        audioStatus.setAudioState(AudioStatus.AUDIO_STATE.PLAYING.ordinal());
                        mainActivity.audioStatusList.set(position, audioStatus);
                    } else {
                        // If mediaPlayer is in idle state, start and play mediaPlayer
                        btnPlay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_pause_24));

                        audioStatus.setAudioState(AudioStatus.AUDIO_STATE.PLAYING.ordinal());
                        mainActivity.audioStatusList.set(position, audioStatus);

                        try {
                            MediaPlayerUtils.startAndPlayMediaPlayer(audioPath, (MediaPlayerUtils.Listener) context);

                            audioStatus.setTotalDuration(MediaPlayerUtils.getTotalDuration());
                            mainActivity.audioStatusList.set(position, audioStatus);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }
}
