package com.resume.maker.ui.fragments.resume

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.resume.maker.R
import com.resume.maker.models.*
import com.resume.maker.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_preview.*

class PreviewFragment : Fragment() {

    private var id: String? = null
    private var personalDetails: PersonalDetailsModel? = null
    private var experiences: ArrayList<ExperienceModel>? = null
    private var languages: ArrayList<SecondaryDetailsModel>? = null
    private var skills: ArrayList<SecondaryDetailsModel>? = null
    private var projects: ArrayList<ProjectModel>? = null
    private var educations: ArrayList<EducationModel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getString("id")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_preview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        viewModel.getPersonalDetails(id).observe(viewLifecycleOwner, {
            personalDetails = it
            getDetails(viewModel)
        })
    }

    private fun getDetails(viewModel: MainViewModel) {
        viewModel.getExperiences(id).observe(viewLifecycleOwner, { it ->
            experiences = it
            viewModel.getLanguages(id).observe(viewLifecycleOwner, { it0 ->
                languages = it0
                viewModel.getSkills(id).observe(viewLifecycleOwner, { it1 ->
                    skills = it1
                    viewModel.getProjects(id).observe(viewLifecycleOwner, { it2 ->
                        projects = it2
                        viewModel.getEducations(id).observe(viewLifecycleOwner, {
                            educations = it
                            progress_circular_preview.visibility = View.GONE
                            preview.visibility = View.VISIBLE
                            setUpPreview()
                        })
                    })
                })
            })
        })
    }

    private fun setUpPreview() {
        var htmlContent = StringBuilder()


    }
}