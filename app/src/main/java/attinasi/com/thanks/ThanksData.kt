package attinasi.com.thanks

open class ThanksData(t: String, txt: String) {

    val title = t
    val text = txt
    val thanksImage: String? = null
}

class ThanksDataStore {

    companion object {
        val instance: ThanksDataStore by lazy { Holder._instance }
    }

    init {
        //loadData()
    }

    fun count(): Int {return data.size}

    fun add(thanksData: ThanksData) {
        data.add(thanksData)
    }

    fun remove(thanksData: ThanksData) {
        val pos = data.indexOf(thanksData)
        if (pos > 0) {
            data.remove(thanksData)
            deletedData.add(DeletedThanksData(pos, thanksData))
        }
    }

    fun restore(thanksData: ThanksData) {
        if (deletedData.contains(thanksData)) {
            val d = deletedData.find { it == thanksData }
            if (d != null) {
                val pos = d.position
                data.add(pos, thanksData)
            }
        }
    }

    fun replace(thanksData: ThanksData, newData: ThanksData) {
        val index = data.lastIndexOf(thanksData)
        if (index > -1) {
            data.removeAt(index)
            data.add(index, newData)
        }
    }

    fun get(position: Int) : ThanksData {
        return data[position]
    }

    fun storeData() {

    }

    public fun loadData() {
        for (i in 1..100) {
            instance.add(ThanksData("Thanks number ${i}", "I am thankful for ${i} ${if (i == 1) "thing" else "things"}..."))
        }
    }

    private val data: ArrayList<ThanksData> = ArrayList()
    private val deletedData: ArrayList<DeletedThanksData> = ArrayList()

    private object Holder {
        val _instance = ThanksDataStore()
    }

    internal class DeletedThanksData(pos: Int, thanks: ThanksData) : ThanksData(thanks.title, thanks.text) {
        val position: Int = pos
    }

}