package attinasi.com.thanks

import android.app.Activity
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.content_home.*
import kotlinx.android.synthetic.main.thanks_card.view.*



public val UNSELECTED_ELEVATION = 10.0F
public val SELECTED_ELEVATION = 40.0F

class HomeActivity : AppCompatActivity() {

    private val layoutManager = LinearLayoutManager(this)
    private val thanksAdapter = ThanksAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)

        initializeThanksListing()
        initializeActionButton()
        setUpItemTouchHelper()
    }

    private fun initializeThanksListing() {
        thanks_recycler.layoutManager = layoutManager
        thanks_recycler.adapter = thanksAdapter

        ThanksDataStore.instance.loadData()
    }

    private fun initializeActionButton() {
        fab.setOnClickListener { view ->
            giveThanks()
        }
    }

    private fun giveThanks() {
        ThanksDialogFragment().show(fragmentManager, "NEW_THANKS_DIALOG")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setUpItemTouchHelper() {

        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            // we want to cache these and not allocate anything repeatedly in the onChildDraw method
            internal var background: Drawable? = null
            internal var deleteIcon: Drawable? = null
            internal var deleteIconMargin: Int = 0
            internal var initiated: Boolean = false

            private fun init() {
                background = ColorDrawable(Color.rgb( 255, 172, 97))
                deleteIcon = ContextCompat.getDrawable(this@HomeActivity, R.drawable.ic_delete_forever_black_24dp)
                deleteIcon?.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
                deleteIconMargin = this@HomeActivity.getResources().getDimension(R.dimen.ic_clear_margin).toInt()
                initiated = true
            }

            // not important, we don't want drag & drop
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun getSwipeDirs(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
                if (viewHolder != null && recyclerView != null) {
                    val position = viewHolder.adapterPosition
                    val adapter = recyclerView.adapter as ThanksAdapter
                    if (adapter.isUndoOn() && adapter.isPendingRemoval(position)) {
                        return 0
                    }
                }
                return super.getSwipeDirs(recyclerView, viewHolder)
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                val swipedPosition = viewHolder.adapterPosition
                val adapter = thanks_recycler.getAdapter() as ThanksAdapter
                val undoOn = adapter.isUndoOn()
                if (undoOn) {
                    adapter.pendingRemoval(swipedPosition)
                } else {
                    adapter.remove(swipedPosition)
                }
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                val itemView = viewHolder.itemView

                // not sure why, but this method get's called for viewholder that are already swiped away
                if (viewHolder.adapterPosition == -1) {
                    // not interested in those
                    return
                }

                if (!initiated) init()

                // draw red background
                background?.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                background?.draw(c)

                // draw x mark
                val itemHeight = itemView.bottom - itemView.top
                val intrinsicWidth = deleteIcon?.intrinsicWidth ?: 0
                val intrinsicHeight = deleteIcon?.intrinsicWidth ?: 0

                val xMarkLeft = itemView.right - deleteIconMargin - intrinsicWidth
                val xMarkRight = itemView.right - deleteIconMargin
                val xMarkTop = itemView.top + (itemHeight - intrinsicHeight) / 2
                val xMarkBottom = xMarkTop + intrinsicHeight
                deleteIcon?.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom)

                deleteIcon?.draw(c)

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }

        }
        val mItemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        mItemTouchHelper.attachToRecyclerView(thanks_recycler)
    }

    class ThanksViewHolder(v: CardView?) : RecyclerView.ViewHolder(v) {
        val card = v

        fun initialize(position: Int) {
            if (card != null ) {
                val data = ThanksDataStore.instance.get(position);

                card.card_layout?.card_title_text?.text = data.title
                card.card_layout?.card_text_view?.setText(data.text)
                card.tag = position
                card.cardElevation = UNSELECTED_ELEVATION

            } else {
                Log.e(THANKS_TAG, "Missing card at position $position")
            }
        }
    }

    class ThanksAdapter(a: Activity) : RecyclerView.Adapter<ThanksViewHolder>() {

        val activity = a

        override fun onBindViewHolder(holder: ThanksViewHolder?, position: Int) {
            Log.d(THANKS_TAG, "ThanksAdapter.onBindView - ${position}")

            if (holder != null) {
                holder.initialize(position)
            }
        }

        override fun getItemCount(): Int {
            return ThanksDataStore.instance.count()
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ThanksViewHolder {
            Log.d(THANKS_TAG, "ThanksAdapter.onCreateView")

            if (parent != null) {
                try {
                    val layout = LayoutInflater.from(parent.context).inflate(R.layout.thanks_card, parent, false) as LinearLayout
                    val card = layout.card_view as CardView
                    layout.removeView(card)
                    card.card_layout.setOnClickListener {
                        card.cardElevation = SELECTED_ELEVATION
                        editCard(card)
                    }

                    return ThanksViewHolder(card)
                } catch (ex: Exception) {
                    Log.e(THANKS_TAG, "Exception!", ex)
                    return ThanksViewHolder(null)
                }
            } else {
                return ThanksViewHolder(null)
            }
        }

        private fun editCard(card: CardView) {
            val editDialog = ThanksDialogFragment()
            val index = card.getTag() as Int
            editDialog.data = ThanksDataStore.instance.get(index)
            editDialog.editComplete = {
                notifyItemChanged(index)
                card.cardElevation = UNSELECTED_ELEVATION
            }
            editDialog.editCancel = {
                card.cardElevation = UNSELECTED_ELEVATION
            }
            editDialog.show(activity.fragmentManager, "EDIT_THANKS_DIALOG")

        }

        fun isUndoOn(): Boolean {
            return false
        }

        fun pendingRemoval(swipedPosition: Int) {

        }

        fun  isPendingRemoval(position: Int): Boolean {
            return false
        }

        fun  remove(swipedPosition: Int) {
            val item = ThanksDataStore.instance.get(swipedPosition)
            ThanksDataStore.instance.remove(item)
            notifyItemRemoved(swipedPosition);
        }

    }
}

