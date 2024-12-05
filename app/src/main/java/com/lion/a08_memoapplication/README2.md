# 검색 기능 구현

### SearchFragment 생성

### Util - Values - FragmentName에 SearchFragment 추가
```kotlin
    // 메모 검색 화면
    SEARCH_MEMO_ALL_FRAGMENT(6, "SearchMemoAllFragment")
```
### MainActivity에 Fragment 객체 추가
```kotlin
            // 메모 검색 화면
            FragmentName.SEARCH_MEMO_ALL_FRAGMENT -> SearchMemoAllFragment(
```
### SearchFragment 화면 구성

[fragment_search_memo_all.xml]
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:transitionGroup="true"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    tools:context=".fragment.SearchMemoAllFragment">
    
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:padding="10dp">

        <com.google.android.material.appbar.MaterialToolbar
            android:id="@+id/toolbarSearchMemoAll"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_weight="0.3"
            android:background="@android:color/transparent"
            android:minHeight="?attr/actionBarSize"
            android:theme="?attr/actionBarTheme" />

        <com.google.android.material.textfield.TextInputLayout
            android:id="@+id/textFieldSearchMemoAllTitle"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginEnd="10dp"
            android:layout_weight="0.05"
            app:endIconMode="clear_text"
            app:startIconDrawable="@drawable/search_24px">

            <com.google.android.material.textfield.TextInputEditText
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:imeOptions="actionSearch"
                android:singleLine="true" />
        </com.google.android.material.textfield.TextInputLayout>

    </LinearLayout>

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recyclerViewSearchMemoAll"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
</LinearLayout>
```

### res - menu 폴더에 toolbar_show_memo_all_menu 생성

### 검색 메뉴 추가

[toolbar_show_memo_all_menu.xml]
```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">

    <item
        android:id="@+id/show_toolbar_menu_Search"
        android:icon="@drawable/search_24px"
        android:title="메모 검색"
        app:showAsAction="always" />
</menu>
```

### MemoDao에 메모 제목 컬럼의 값이 지정된 값과 같은 행만 가져오는 메서드 구현

[MemoDao.kt]
```kotlin
    // 메모 제목 컬럼의 값이 지정된 값과 같은 행만 가져오는 메서드
    @Query("""
        select * from MemoTable
        where memoTitle = :memoTitle
        order by memoIdx desc
    """)
    fun selectMemoDataAllByMemoName(memoTitle: String) : List<MemoVO>
```

### MemoRepository에 메모 제목을 검색해 제품 데이터 전체를 가져오는 메서드를 구현

[MemoRepository.kt]
```kotlin
// 메모 제목으로 검색하여 제품 데이터 전체를 가져오는 메서드
        fun selectMemoDataAllByMemoName(context: Context, memoTitle:String): MutableList<MemoModel> {
            // 데이터를 가져온다.
            val memoDataBase = MemoDataBase.getInstance(context)
            val memoList = memoDataBase?.memoDao()?.selectMemoDataAllByMemoName(memoTitle)

            // 제품 데이터를 담을 리스트
            val tempList = mutableListOf<MemoModel>()

            // 제품 수만큼 반복
            memoList?.forEach {
                val memoModel = MemoModel(
                    it.memoIdx,
                    it.memoTitle,
                    it.memoText,
                    it.memoIsSecret,
                    it.memoIsFavorite,
                    it.memoPassword.toString(),
                    it.memoCategoryIdx
                )
                // 리스트에 담는다.
                tempList.add(memoModel)
            }
            return tempList
        }
```

### SearchMemoAllFragment 구현
```kotlin
class SearchMemoAllFragment : Fragment() {

    lateinit var fragmentSearchMemoAllBinding:FragmentSearchMemoAllBinding
    lateinit var mainActivity: MainActivity

    var memoList = mutableListOf<MemoModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentSearchMemoAllBinding = FragmentSearchMemoAllBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        // 툴바 구성 메서드 호출
        settingToolbarSearchStudent()
        // RecyclerView 구성 메서드 호출
        settingRecyclerView()
        // 입력 요소 설정 메서드 호출
        settingTextField()

        return fragmentSearchMemoAllBinding.root
    }

    // 툴바를 구성하는 메서드
    fun settingToolbarSearchStudent(){
        fragmentSearchMemoAllBinding.apply {
            toolbarSearchMemoAll.title = "메모 제목 검색"

            toolbarSearchMemoAll.setNavigationIcon(R.drawable.arrow_back_24px)
            toolbarSearchMemoAll.setNavigationOnClickListener {
                mainActivity.removeFragment(FragmentName.SEARCH_MEMO_ALL_FRAGMENT)
            }
        }
    }

    // RecyclerView를 구성하는 메서드
    fun settingRecyclerView(){
        fragmentSearchMemoAllBinding.apply {
            // 어뎁터
            recyclerViewSearchMemoAll.adapter = RecyclerViewSearchMemoAllAdapter()
            // LayoutManager
            recyclerViewSearchMemoAll.layoutManager = GridLayoutManager(mainActivity, 2)
            // 구분선
            val deco = MaterialDividerItemDecoration(mainActivity, MaterialDividerItemDecoration.VERTICAL)
            recyclerViewSearchMemoAll.addItemDecoration(deco)
        }
    }

    // Recyclerview의 어뎁터
    inner class RecyclerViewSearchMemoAllAdapter : RecyclerView.Adapter<RecyclerViewSearchMemoAllAdapter.ViewHolderDeviceSearch>(){
        inner class ViewHolderDeviceSearch(var rowMemoBinding: RowMemoBinding) : RecyclerView.ViewHolder(rowMemoBinding.root), OnClickListener {
            override fun onClick(v: View?) {
                // 메모 번호를 담는다.
                val dataBundle = Bundle()
                dataBundle.putInt("memoIdx", memoList[adapterPosition].memoIdx)
                // 메모를 보는 화면으로 이동
                mainActivity.replaceFragment(FragmentName.READ_MEMO_FRAGMENT, true, true, dataBundle)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderDeviceSearch {
            val rowMemoBinding = RowMemoBinding.inflate(layoutInflater, parent, false)
            val viewHolderDeviceSearch = ViewHolderDeviceSearch(rowMemoBinding)
            return viewHolderDeviceSearch
        }

        override fun getItemCount(): Int {
            return memoList.size
        }

        override fun onBindViewHolder(holder: ViewHolderDeviceSearch, position: Int) {
            holder.rowMemoBinding.textViewRowTitle.text = memoList[position].memoTitle
        }
    }

    // 입력 요소 설정
    fun settingTextField() {
        fragmentSearchMemoAllBinding.apply {
            // 검색창에 포커스를 둔다
            mainActivity.showSoftInput(textFieldSearchMemoAllTitle.editText!!)
            // 키보드의 엔터를 누르면 동작하는 리스너
            textFieldSearchMemoAllTitle.editText?.setOnEditorActionListener { v, actionId, event ->
                // 검색 데이터를 가져와 보여준다.
                CoroutineScope(Dispatchers.Main).launch {
                    val work1 = async(Dispatchers.IO) {
                        val keyword = textFieldSearchMemoAllTitle.editText?.text.toString()
                        MemoRepository.selectMemoDataAllByMemoName(mainActivity, keyword)
                    }
                    memoList = work1.await()
                    recyclerViewSearchMemoAll.adapter?.notifyDataSetChanged()
                }
                mainActivity.hideSoftInput()
                true
            }

        }
    }
}
```

### ShowMemoAllFragment 툴바 설정 메서드에 메뉴 코드를 추가
```kotlin
            // 메뉴
            toolbarShowMemoAll.inflateMenu(R.menu.toolbar_show_memo_all_menu)
            toolbarShowMemoAll.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.show_toolbar_menu_Search -> {
                        mainActivity.replaceFragment(FragmentName.SEARCH_MEMO_ALL_FRAGMENT, true, true, null)
                    }
                }
                true
            }
```