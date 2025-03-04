package com.example.lab1.music

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.lab1.R

class MusicFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_music, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val startButton: Button = view.findViewById(R.id.startButton)
        val pauseButton: Button = view.findViewById(R.id.pauseButton)
        val stopButton: Button = view.findViewById(R.id.stopButton)
        val nextButton: Button = view.findViewById(R.id.nextButton)
        val prevButton: Button = view.findViewById(R.id.prevButton)

        startButton.setOnClickListener { sendCommandToService(MusicService.ACTION_PLAY) }
        pauseButton.setOnClickListener { sendCommandToService(MusicService.ACTION_PAUSE) }
        stopButton.setOnClickListener { sendCommandToService(MusicService.ACTION_STOP) }
        nextButton.setOnClickListener { sendCommandToService(MusicService.ACTION_NEXT) }
        prevButton.setOnClickListener { sendCommandToService(MusicService.ACTION_PREVIOUS) }
    }

    private fun sendCommandToService(action: String) {
        val intent = Intent(requireContext(), MusicService::class.java).apply { this.action = action }
        requireContext().startService(intent)
    }
}