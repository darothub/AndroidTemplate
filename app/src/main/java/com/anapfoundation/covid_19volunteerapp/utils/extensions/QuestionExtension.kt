package com.anapfoundation.covid_19volunteerapp.utils.extensions

import android.content.Context
import android.content.res.Resources
import androidx.core.content.ContentProviderCompat.requireContext
import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.model.ReportQuestionModel

fun Context.indexCases():ReportQuestionModel{
    return ReportQuestionModel(localized(R.string.awareness_of_covid),
        localized(R.string.awareness_of_covid),
        null,
        listOf(localized(R.string.very_high),
            localized(R.string.high),
            localized(R.string.low),
            localized(R.string.very_low)
        )

    )
}
fun Context.awareness():ReportQuestionModel{
    return ReportQuestionModel(localized(R.string.index_cases),
        localized(R.string.index_cases_reported),
        null,
        listOf(localized(R.string.infections),
            localized(R.string.death))
    )
}

fun Context.publicEnglightenment():ReportQuestionModel{
    return ReportQuestionModel(localized(R.string.public_enlightenment),
        localized(R.string.public_enlightenment),
        null,
        listOf(localized(R.string.adequate),
            localized(R.string.very_adequate),
            localized(R.string.inadequate)
        )
    )
}

fun Context.measures():ReportQuestionModel{
    return ReportQuestionModel(localized(R.string.measures),
        localized(R.string.measures),
        null,
        listOf(localized(R.string.curfew),
            localized(R.string.total_lockdown),
            localized(R.string.partial_lockdown)
        )
    )
}

fun Context.compliance():ReportQuestionModel{
    return ReportQuestionModel(localized(R.string.level_of_compliance),
        localized(R.string.level_of_compliance),
        null,
        listOf(localized(R.string.high_total),
            localized(R.string.average_largely),
            localized(R.string.low_partial)
        )
    )
}

fun Context.challenges():ReportQuestionModel{
    return ReportQuestionModel(localized(R.string.challenge_observed),
        localized(R.string.challenge_observed),
        null,
        listOf(localized(R.string.defiance),
            localized(R.string.no_surveillance),
            localized(R.string.security_forces)
        )
    )
}

fun Context.palliatives():ReportQuestionModel{
    return ReportQuestionModel(localized(R.string.any_govt_palliatives),
        localized(R.string.any_govt_palliatives),
        null,
        listOf(localized(R.string.food),
            localized(R.string.money),
            localized(R.string.material)
        )
    )
}






fun ReportQuestionModel.context():Context{
    return context()
}