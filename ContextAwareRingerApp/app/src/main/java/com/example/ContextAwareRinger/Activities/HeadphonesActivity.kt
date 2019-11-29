package com.example.ContextAwareRinger.Activities;

import android.app.Activity;
import android.media.AudioManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.ContextAwareRinger.R

//TODO: look into making this class a fragment
class HeadphonesActivity(private val volumeMap : MutableMap<String, Int>) : Fragment() {

    private val TAG = "HeadphonesActivity"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater!!.inflate(R.layout.headphones, container, false)
        var radioGroup: RadioGroup = rootView.findViewById(R.id.headphonesRadio) as RadioGroup

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            Toast.makeText(context!!, " Is this working??", Toast.LENGTH_LONG)
            //var selected = rootView.findViewById<RadioButton>(radioGroup.checkedRadioButtonId)
            addHeadphoneFence(checkedId)
        }


        return rootView

        //TODO: Need to use Shared preference so that whenever this fragment is launched, the last preference gets selected.
    }

    private fun addHeadphoneFence(checkId: Int){
        var ringerMode = -1


        if(checkId == R.id.radioButtonHeadphone1){
            ringerMode = AudioManager.RINGER_MODE_SILENT
            Toast.makeText(context!!, "Do Not Disturb selected " + ringerMode.toString(), Toast.LENGTH_LONG).show()
        }
        else if(checkId == R.id.radioButtonHeadphone2){
            ringerMode = AudioManager.RINGER_MODE_VIBRATE
            Toast.makeText(context!!, "Vibrate Selected " + ringerMode.toString(), Toast.LENGTH_LONG).show()
        }
        else if(checkId == R.id.radioButtonHeadphone3){
            ringerMode = AudioManager.RINGER_MODE_NORMAL
            Toast.makeText(context!!, "Normal Selected " + ringerMode.toString(), Toast.LENGTH_LONG).show()
        }

    }
}
