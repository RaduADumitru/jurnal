package com.example.proiect_jurnal

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    private var postsList = mutableListOf<Post>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PostsAdapter
    private lateinit var searchView: SearchView
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var userReference: DatabaseReference

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        adapter = PostsAdapter(postsList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Initialize Firebase Auth and Database
        auth = Firebase.auth
        database = Firebase.database.reference

        val userId = auth.currentUser!!.uid

        // Populate the text posts list with data from Firebase or any other data source
        userReference = database.child("posts").child(userId)
        userReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnapshot in dataSnapshot.children) {
                    val postId = postSnapshot.key
                    val post = postSnapshot.getValue(Post::class.java)
                    if (post != null) {
                        postsList.add(post)
                    }
                }
                //show starting with most recent posts
                postsList.reverse()
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Handle potential error
                // ...
                throw Error()
            }
        })

        searchView = view.findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }

        })
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
    }

    private fun getDummyTextPosts(): Collection<Post> {
        return listOf<Post>(Post(postId = "1", title = "Post1", content = "Desc1", uid = ""), Post(postId = "2", title = "Post2", content = "Desc2", uid = ""))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

}