package eu.selfhost.riegel.superfit.ui

import android.app.AlertDialog
import android.content.Context
import android.preference.ListPreference
import android.util.AttributeSet

class DynamicListPreference(context: Context, attrs: AttributeSet)
	: ListPreference(context, attrs)
{

	fun setLoadingListener(loadingListener: (listPreference: ListPreference) -> Unit)
	{
		this.loadingListener = loadingListener
	}

	override fun onPrepareDialogBuilder(builder: AlertDialog.Builder)
	{
		loadingListener(this)
		super.onPrepareDialogBuilder(builder)
	}

	private lateinit var loadingListener: (listPreference: ListPreference) -> Unit
}