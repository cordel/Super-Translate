package live.senya.supertranslate.translate


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import live.senya.supertranslate.R

class TranslateFragment : Fragment() {

    companion object {
        fun newInstance(): TranslateFragment {
            return TranslateFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_translate, container, false)
    }
}
