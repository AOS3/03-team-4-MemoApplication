package com.lion.a08_memoapplication.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.lion.a08_memoapplication.MainActivity
import com.lion.a08_memoapplication.R
import com.lion.a08_memoapplication.databinding.FragmentSearchMemoAllBinding
import com.lion.a08_memoapplication.databinding.RowMemoBinding
import com.lion.a08_memoapplication.model.MemoModel
import com.lion.a08_memoapplication.repository.MemoRepository
import com.lion.a08_memoapplication.util.FragmentName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


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

    // Recyclerview의 어댑터
    inner class RecyclerViewSearchMemoAllAdapter : RecyclerView.Adapter<RecyclerViewSearchMemoAllAdapter.ViewHolderMemoSearch>(){
        inner class ViewHolderMemoSearch(var rowMemoBinding: RowMemoBinding) : RecyclerView.ViewHolder(rowMemoBinding.root), OnClickListener {
            override fun onClick(v: View?) {
                // 메모 번호를 담는다.
                val dataBundle = Bundle()
                dataBundle.putInt("memoIdx", memoList[adapterPosition].memoIdx)
                // 메모를 보는 화면으로 이동
                mainActivity.replaceFragment(FragmentName.READ_MEMO_FRAGMENT, true, true, dataBundle)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderMemoSearch {
            val rowMemoBinding = RowMemoBinding.inflate(layoutInflater, parent, false)
            val viewHolderMemoSearch = ViewHolderMemoSearch(rowMemoBinding)
            rowMemoBinding.root.setOnClickListener(viewHolderMemoSearch)
            return viewHolderMemoSearch
        }

        override fun getItemCount(): Int {
            return memoList.size
        }

        override fun onBindViewHolder(holder: ViewHolderMemoSearch, position: Int) {
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