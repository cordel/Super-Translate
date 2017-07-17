package live.senya.supertranslate.data

import android.os.Parcel
import android.os.Parcelable

/**
 * Immutable class that represents Language.
 */
data class Lang(val code: String,
                val name: String,
                val locale: String) : Parcelable {

  companion object {
    @JvmField val CREATOR: Parcelable.Creator<Lang> = object : Parcelable.Creator<Lang> {
      override fun createFromParcel(source: Parcel): Lang = Lang(source)
      override fun newArray(size: Int): Array<Lang?> = arrayOfNulls(size)
    }
  }

  constructor(source: Parcel) : this(
      source.readString(),
      source.readString(),
      source.readString()
  )

  override fun describeContents() = 0

  override fun writeToParcel(dest: Parcel, flags: Int) {
    dest.writeString(code)
    dest.writeString(name)
    dest.writeString(locale)
  }

}

