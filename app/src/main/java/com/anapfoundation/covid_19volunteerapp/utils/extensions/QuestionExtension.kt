package com.anapfoundation.covid_19volunteerapp.utils.extensions

import android.content.Context
import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.model.ReportQuestionModel

fun Context.indexCases():ReportQuestionModel{
    return ReportQuestionModel(getLocalisedString(R.string.awareness_of_covid),
        getLocalisedString(R.string.awareness_of_covid),
        null,
        listOf(getLocalisedString(R.string.very_high),
            getLocalisedString(R.string.high),
            getLocalisedString(R.string.low),
            getLocalisedString(R.string.very_low)
        )

    )
}
fun Context.awareness():ReportQuestionModel{
    return ReportQuestionModel(getLocalisedString(R.string.index_cases),
        getLocalisedString(R.string.index_cases_reported),
        null,
        listOf(getLocalisedString(R.string.infections),
            getLocalisedString(R.string.death))
    )
}

fun Context.publicEnglightenment():ReportQuestionModel{
    return ReportQuestionModel(getLocalisedString(R.string.public_enlightenment),
        getLocalisedString(R.string.public_enlightenment),
        null,
        listOf(getLocalisedString(R.string.adequate),
            getLocalisedString(R.string.very_adequate),
            getLocalisedString(R.string.inadequate)
        )
    )
}

fun Context.measures():ReportQuestionModel{
    return ReportQuestionModel(getLocalisedString(R.string.measures),
        getLocalisedString(R.string.measures),
        null,
        listOf(getLocalisedString(R.string.curfew),
            getLocalisedString(R.string.total_lockdown),
            getLocalisedString(R.string.partial_lockdown)
        )
    )
}

fun Context.compliance():ReportQuestionModel{
    return ReportQuestionModel(getLocalisedString(R.string.level_of_compliance),
        getLocalisedString(R.string.level_of_compliance),
        null,
        listOf(getLocalisedString(R.string.high_total),
            getLocalisedString(R.string.average_largely),
            getLocalisedString(R.string.low_partial)
        )
    )
}

fun Context.challenges():ReportQuestionModel{
    return ReportQuestionModel(getLocalisedString(R.string.challenge_observed),
        getLocalisedString(R.string.challenge_observed),
        null,
        listOf(getLocalisedString(R.string.defiance),
            getLocalisedString(R.string.no_surveillance),
            getLocalisedString(R.string.security_forces)
        )
    )
}

fun Context.palliatives():ReportQuestionModel{
    return ReportQuestionModel(getLocalisedString(R.string.any_govt_palliatives),
        getLocalisedString(R.string.any_govt_palliatives),
        null,
        listOf(getLocalisedString(R.string.food),
            getLocalisedString(R.string.money),
            getLocalisedString(R.string.material)
        )
    )
}






fun ReportQuestionModel.context():Context{
    return context()
}