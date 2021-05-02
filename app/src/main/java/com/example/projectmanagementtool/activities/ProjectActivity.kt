package com.example.projectmanagementtool.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectmanagementtool.R
import com.example.projectmanagementtool.adapters.LogItemAdapter
import com.example.projectmanagementtool.data.viewmodels.ProjectViewModel
import com.example.projectmanagementtool.models.Project
import com.example.projectmanagementtool.models.User
import com.example.projectmanagementtool.popups.CreateLogDialog
import com.example.projectmanagementtool.utils.Constants
import com.example.projectmanagementtool.utils.UtilityFunctions
import kotlinx.android.synthetic.main.activity_project.*
import kotlinx.android.synthetic.main.create_log_dialog.*

class ProjectActivity : AppCompatActivity() {
    private lateinit var mAdapter: LogItemAdapter
    lateinit var projectViewModel: ProjectViewModel
    lateinit var mProjectDocumentId: String
    lateinit var mProject: Project
    lateinit var mComment: String
    lateinit var mCommand: String
    lateinit var mDialog: Dialog
    lateinit var mUser :User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project)
        if (intent.hasExtra(Constants.DOCUMENT_ID)) {
            mProjectDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID)!!

        }
        if (intent.hasExtra(Constants.USER)) {
            mUser = intent.getParcelableExtra(Constants.USER)!!

        }
        mAdapter = LogItemAdapter(this)
        setUpRecyclerView()
        projectViewModel =
            ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application)).get(
                ProjectViewModel::class.java
            )
        subscribeToObservers()
        projectViewModel.projectDetails(mProjectDocumentId)


        btnNewChanges.setOnClickListener {
            if (::mProject.isInitialized) {
                mDialog = Dialog(this)
                mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                mDialog.setContentView(R.layout.create_log_dialog)
                mDialog.show()
                mDialog.btnMakeChanges.setOnClickListener {
                    mComment = mDialog.etLogDescriptionCreateLog.text.toString()
                    val currentTime = UtilityFunctions().getCurrentTime()
                    if(!::mCommand.isInitialized){
                        mCommand = "None"
                    }
                    val log = com.example.projectmanagementtool.models.Log(name = mCommand,description = mComment,
                    createdBy = mUser.name,createdAt = currentTime,image = mUser.image)
                    projectViewModel.addLogToProject(mProject,log)
                    Toast.makeText(this, "Make some changes", Toast.LENGTH_SHORT).show()
                    mDialog.dismiss()
                    projectViewModel.projectDetails(mProjectDocumentId)
                }
            }
        }

    }

    private fun setUpRecyclerView() {
        rvLogList.adapter = mAdapter
        rvLogList.layoutManager = LinearLayoutManager(
            this@ProjectActivity,
            LinearLayoutManager.VERTICAL, false
        )
        mAdapter.setOnClickListener(object : LogItemAdapter.OnClickListener {
            override fun onClick(
                position: Int,
                model: com.example.projectmanagementtool.models.Log
            ) {
                Toast.makeText(this@ProjectActivity, "Clicked", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun subscribeToObservers(){
        projectViewModel.mProject.observe(this, Observer { project ->
            Log.d("debug",project.logList.toString())
            fillDetails()
            mProject = project
            mAdapter.setData(project.logList)
        })
    }

    fun onRadioButtonClicked(view: View) {
        Log.d("debug", "radio group")
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.radio_merge ->
                    if (checked) {
                        mCommand = "Merge"
                        // Merge
                    }
                R.id.radio_commit ->
                    if (checked) {
                        mCommand = "Commit"
                        // Commit
                    }
                R.id.radio_push ->
                    if (checked) {
                        mCommand = "Push"
                        // Push
                    }

            }
        }
    }


    private fun fillDetails() {
        if (::mProject.isInitialized) {
            projectNameProjectActivity.text = mProject.name
            projectDescriptionProjectActivity.text = mProject.description
            projectGithubRepo.text = mProject.githubRepoUrl
        }
    }
}