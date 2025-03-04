package com.example.lab1.calendar

import android.Manifest
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lab1.databinding.FragmentCalendarBinding

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CalendarAdapter
    private val eventsList = mutableListOf<CalendarEvent>()

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CalendarAdapter(eventsList)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        checkPermissionsAndLoadEvents()
    }

    private fun checkPermissionsAndLoadEvents() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CALENDAR)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_CALENDAR),
                PERMISSION_REQUEST_CODE
            )
        } else {
            loadCalendarEvents()
        }
    }

    private fun loadCalendarEvents() {
        val resolver: ContentResolver = requireContext().contentResolver
        val uri = CalendarContract.Events.CONTENT_URI
        val projection = arrayOf(
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DTSTART
        )

        val cursor: Cursor? = resolver.query(uri, projection, null, null, CalendarContract.Events.DTSTART + " ASC")

        cursor?.use {
            eventsList.clear()
            while (it.moveToNext()) {
                val title = it.getString(0)
                val date = it.getLong(1)
                eventsList.add(CalendarEvent(title, date))
            }
            adapter.notifyDataSetChanged()
        } ?: Toast.makeText(requireContext(), "Нет событий в календаре", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}