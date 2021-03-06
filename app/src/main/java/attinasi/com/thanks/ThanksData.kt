package attinasi.com.thanks

import java.security.InvalidParameterException

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
        if (pos >= 0) {
            data.remove(thanksData)
            deletedData.add(DeletedThanksData(pos, thanksData))
        } else {
            throw InvalidParameterException()
        }
    }

    fun restore(thanksData: ThanksData) {
        val d = deletedData.find {
            it.title == thanksData.title &&
            it.text == thanksData.text &&
            it.thanksImage == thanksData.thanksImage
        }
        if (d != null) {
            val pos = d.position
            data.add(pos, thanksData)
            deletedData.remove(d)
        } else {
            throw InvalidParameterException()
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

    public fun reset() {
        data.removeAll { true }
        deletedData.removeAll { true }
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