package live.senya.supertranslate.history


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
import live.senya.supertranslate.data.Translation
import live.senya.supertranslate.main.MainActivity

class HistoryFragment : Fragment(), HistoryContract.View {

  companion object {
    private val TAG = HistoryFragment::class.java.simpleName!!

    fun newInstance(): HistoryFragment {
      return HistoryFragment()
    }
  }

  val linearLayoutManager = LinearLayoutManager(context)
  lateinit var historyAdapter: HistoryRecyclerViewAdapter
  lateinit var historyPresenter: HistoryContract.Presenter

  override fun onCreate(savedInstanceState: Bundle?) {
    Log.i(HistoryFragment.TAG, "onCreate()")
    super.onCreate(savedInstanceState)
    historyAdapter = HistoryRecyclerViewAdapter(context, mutableListOf(), { historyPresenter.onTranslationSelected(it) })
  }

  override fun onCreateView(inflater: LayoutInflater?,
                            container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    Log.i(HistoryFragment.TAG, "onCreateView()")
    val root = inflater!!.inflate(R.layout.fragment_history, container, false)

    with(root.findViewById(R.id.rv_history) as RecyclerView) {
      layoutManager = linearLayoutManager
      adapter = historyAdapter
    }

    historyPresenter.loadHistory()
    historyPresenter.getHistoryUpdates()

    return root
  }

  override fun setPresenter(presenter: HistoryContract.Presenter) {
    historyPresenter = presenter
  }

  override fun showHistory(history: MutableList<Translation>) {
    historyAdapter.replaceData(history)
  }

  override fun addTranslationToTheTop(translation: Translation) {
    historyAdapter.addItemToTheTop(translation)
    linearLayoutManager.scrollToPositionWithOffset(0, 0)
  }

  override fun switchToTranslationView(translation: Translation) {
    (activity as MainActivity).switchFragmentToTranslationView()
  }

  class HistoryRecyclerViewAdapter(
      val context: Context,
      var history: MutableList<Translation>,
      val onItemClick: (Translation) -> Unit
  ) : RecyclerView.Adapter<HistoryRecyclerViewAdapter.Companion.ViewHolder>() {

    companion object {
      class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTranslation = itemView.findViewById(R.id.title) as TextView

        fun bind(translation: Translation, onItemClick: (Translation) -> Unit) {
          textViewTranslation.text = translation.translatedText
          itemView.setOnClickListener { onItemClick(translation) }
        }
      }
    }

    override fun onBindViewHolder(holder: HistoryRecyclerViewAdapter.Companion.ViewHolder, position: Int) {
      holder.bind(history[position], onItemClick)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryRecyclerViewAdapter.Companion.ViewHolder {
      val inflater = LayoutInflater.from(context)
      val view = inflater.inflate(R.layout.item_lang, parent, false)
      return ViewHolder(view)
    }

    override fun getItemCount(): Int = history.size

    fun replaceData(history: MutableList<Translation>) {
      this.history = history
      this.history.reverse()
      notifyDataSetChanged()
    }

    fun addItemToTheTop(translation: Translation) {
      if (history.contains(translation)) {
        val position = history.indexOf(translation)
        history.removeAt(position)
        history.add(0, translation)
        notifyItemMoved(position, 0)
      } else{
        history.add(0, translation)
        notifyItemInserted(0)
      }
    }
  }
}
