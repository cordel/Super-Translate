package live.senya.supertranslate.langselector

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import live.senya.supertranslate.R
import live.senya.supertranslate.data.Lang
import live.senya.supertranslate.translate.TranslateFragment

class LangSelectorFragment : Fragment(), LangSelectorContract.View {

  companion object {
    private val TAG = LangSelectorFragment::class.java.simpleName!!
    fun newInstance(): LangSelectorFragment = LangSelectorFragment()
  }

  lateinit var langsAdapter: LangsRecyclerViewAdapter
  lateinit var langSelectorPresenter: LangSelectorContract.Presenter

  override fun onCreate(savedInstanceState: Bundle?) {
    Log.i(LangSelectorFragment.TAG, "onCreate()")
    super.onCreate(savedInstanceState)
    langsAdapter = LangsRecyclerViewAdapter(context, emptyList(), { langSelectorPresenter.onLangSelected(it) })
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    Log.i(LangSelectorFragment.TAG, "onCreateView()")
    val root = inflater!!.inflate(R.layout.fragment_lang_selector, container, false)

    with(root.findViewById(R.id.rv_langselector) as RecyclerView) {
      layoutManager = LinearLayoutManager(context)
      adapter = langsAdapter
    }

    langSelectorPresenter.loadLangs()

    return root
  }

  override fun setPresenter(presenter: LangSelectorContract.Presenter) {
    langSelectorPresenter = presenter
  }

  override fun showLangs(langs: List<Lang>) = langsAdapter.replaceData(langs)

  override fun returnSelectedLang(lang: Lang) {
    with(activity) {
      setResult(Activity.RESULT_OK, TranslateFragment.getResultIntent(lang))
      finish()
    }
  }

  class LangsRecyclerViewAdapter(
      val context: Context,
      var langs: List<Lang>,
      val onItemClick: (Lang) -> Unit
  ) : RecyclerView.Adapter<LangsRecyclerViewAdapter.Companion.ViewHolder>() {

    companion object {
      class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewLangName = itemView.findViewById(R.id.title) as TextView

        fun bind(lang: Lang, onItemClick: (Lang) -> Unit) {
          textViewLangName.text = lang.name
          itemView.setOnClickListener { onItemClick(lang) }
        }
      }
    }

    override fun onBindViewHolder(holder: LangsRecyclerViewAdapter.Companion.ViewHolder, position: Int) {
      holder.bind(langs[position], onItemClick)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LangsRecyclerViewAdapter.Companion.ViewHolder {
      val inflater = LayoutInflater.from(context)
      val view = inflater.inflate(R.layout.item_lang, parent, false)
      return ViewHolder(view)
    }

    override fun getItemCount(): Int = langs.size

    fun replaceData(langs: List<Lang>) {
      this.langs = langs
      notifyDataSetChanged()
    }
  }

}
