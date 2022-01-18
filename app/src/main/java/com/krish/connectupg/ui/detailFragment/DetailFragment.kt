package com.krish.connectupg.ui.detailFragment

import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.google.firebase.database.*
import com.krish.connectupg.adapter.CommentAdapter
import com.krish.connectupg.databinding.FragmentDetailBinding
import com.krish.connectupg.model.Comment
import com.krish.connectupg.model.User
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter

private const val TAG = "DetailFragment"

class DetailFragment : Fragment() {
    private lateinit var binding: FragmentDetailBinding
    private val args by navArgs<DetailFragmentArgs>()
    private val mDatabaseReference: DatabaseReference by lazy { FirebaseDatabase.getInstance().reference }
    private val mAdapter: CommentAdapter by lazy { CommentAdapter() }
    private val mComment: ArrayList<Comment> by lazy { ArrayList() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailBinding.inflate(inflater, container, false)

        setUpPost()
        setRecyclerView()
        getCommentsFromDatabase()

        binding.goBack.setOnClickListener {
            val action = DetailFragmentDirections.actionDetailFragment2ToHomeFragment(args.currentUser)
            findNavController().navigate(action)
        }

        binding.btnPost.setOnClickListener {
            val comment = binding.etComment.text.toString()
            if (validateComment(comment)) {
                val currentComment = Comment(
                    args.currentUser?.username!!, comment,
                    args.currentUser!!.uid, args.currentPost?.postTime,
                    System.currentTimeMillis()
                )
                saveCommentToDatabase(currentComment)
                clearField()
            }
        }

        return binding.root
    }

    private fun getCommentsFromDatabase() {
        mDatabaseReference.child("comments").child(args.currentPost?.postTime.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    mComment.clear()
                    for (snap in snapshot.children) {
                        val currentComment = snap.getValue(Comment::class.java)
                        mComment.add(currentComment!!)
                    }
                    Log.d(TAG, "onDataChange: $mComment")
                    mComment.reverse()
                    mAdapter.setData(mComment)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private fun setRecyclerView() {
        val alphaAdapter = AlphaInAnimationAdapter(mAdapter)
        binding.commentRecyclerView.adapter = ScaleInAnimationAdapter(alphaAdapter).apply {
            setDuration(1000)
            setHasStableIds(false)
            setFirstOnly(false)
            setInterpolator(OvershootInterpolator(.100f))
        }
        binding.commentRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun clearField() {
        binding.etComment.text.clear()
    }

    private fun saveCommentToDatabase(comment: Comment) {
        val ref = mDatabaseReference.child("comments").child(comment.postKey.toString())
            .child(comment.commentTime.toString())
        ref.setValue(comment)
            .addOnSuccessListener {
                Log.d(TAG, "saveCommentToDatabase: ")
            }
    }

    private fun validateComment(comment: String): Boolean {
        return if (comment.isBlank()) {
            binding.etComment.error = "Field can't be set empty"
            false
        } else {
            true
        }
    }

    private fun setUpPost() {
        binding.apply {
            mDatabaseReference.child("users").child(args.currentPost?.uid!!)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val currentUser = snapshot.getValue(User::class.java)
                        username.text = currentUser?.username
                        minutesAgo.text =
                            DateUtils.getRelativeTimeSpanString(args.currentPost!!.postTime!!)
                        ivProfilePic.load(currentUser?.imageURL)
                        postTitle.text = args.currentPost!!.title
                        postDescription.text = args.currentPost!!.content
                        binding.postBy.text = "Post by ${currentUser?.username}"
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
        }
    }
}