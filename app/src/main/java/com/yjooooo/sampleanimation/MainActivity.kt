package com.yjooooo.sampleanimation

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.graphics.Path
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import com.yjooooo.sampleanimation.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    var beforeCycle = true
    private lateinit var faceInfo: ViewInfo
    private lateinit var bodyInfo: ViewInfo
    private lateinit var faceRightUpAnimator: Animator
    private lateinit var faceRightDownAnimator: Animator
    private lateinit var faceLeftUpAnimator: Animator
    private lateinit var faceLeftDownAnimator: Animator
    private lateinit var faceDownToBodyAnim: Animator
    private lateinit var faceUpFromBodyAnim: Animator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnMain.setOnClickListener {
            setHorizontalAnimator()
            createAnimation(
                R.animator.animator_face_shape_up,
                binding.ivSnowmanFace
            ).start()
            faceRightUpAnimator.start()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        binding.ivSnowmanFace.post {
            with(binding.ivSnowmanFace) {
                faceInfo = ViewInfo(this.x, this.y, this.width.toFloat(), this.height.toFloat())
            }
        }
        binding.ivSnowmanBody.post {
            with(binding.ivSnowmanBody) {
                bodyInfo = ViewInfo(this.x, this.y, this.width.toFloat(), this.height.toFloat())
            }
        }
    }

    private fun setHorizontalAnimator() {
        val margin = bodyInfo.x - faceInfo.x - faceInfo.width
        val left = faceInfo.x
        val top = bodyInfo.y - faceInfo.height - translateDpToPx(80)
        val right = faceInfo.x + bodyInfo.width + faceInfo.width + margin * 2
        val bottom =
            bodyInfo.y + (bodyInfo.height - faceInfo.height) * 2 + faceInfo.height + translateDpToPx(
                80
            )
        val rightUpPath = Path().apply {
            arcTo(left, top, right, bottom, 180f, 90f, true)
        }
        val rightDownPath = Path().apply {
            arcTo(left, top, right, bottom, 270f, 90f, true)
        }
        val leftUpPath = Path().apply {
            arcTo(left, top, right, bottom, 0f, -90f, true)
        }
        val leftDownPath = Path().apply {
            arcTo(left, top, right, bottom, 270f, -90f, true)
        }

        faceRightUpAnimator = getAnimator(goRightNext = true, goUpNext = false, path = rightUpPath)
        faceRightDownAnimator = getAnimator(goRightNext = false, goUpNext = true, path = rightDownPath)
        faceLeftUpAnimator = getAnimator(goRightNext = false, goUpNext = false, path = leftUpPath)
        faceLeftDownAnimator = getAnimator(goRightNext = true, goUpNext = true, path = leftDownPath)


    }

    private fun setFaceDownToBodyAnim(goRightNext: Boolean, goUpNext: Boolean) {
        faceDownToBodyAnim =
            createAnimation(R.animator.animator_face_down_to_body, binding.ivSnowmanFace).apply {
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        when (goRightNext) {
                            true -> {
                                when (goUpNext) {
                                    true -> { // Next : RightUp
                                        if(beforeCycle) {
                                            setFaceUpFromBodyAnim(true, false)
                                            beforeCycle = false
                                        }
                                    }
                                    false -> { // Next : RightDown
                                    }
                                }
                            }
                            false -> {
                                when (goUpNext) {
                                    true -> { // Next : LeftUp
                                        setFaceUpFromBodyAnim(false, false)
                                    }
                                    false -> { // Next : LeftDown
                                    }
                                }
                            }
                        }
                    }
                })
            }
        faceDownToBodyAnim.start()
    }

    private fun setFaceUpFromBodyAnim(goRightNext: Boolean, goUpNext: Boolean) {
        faceUpFromBodyAnim =
            createAnimation(R.animator.animator_face_up_from_body, binding.ivSnowmanFace).apply {
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        when (goRightNext) {
                            true -> {
                                when (goUpNext) {
                                    true -> { // Next : RightUp
                                    }
                                    false -> { // Next : RightDown
                                        createAnimation(
                                            R.animator.animator_face_shape_down,
                                            binding.ivSnowmanFace
                                        ).start()
                                        faceRightDownAnimator.start()
                                    }
                                }
                            }
                            false -> {
                                when (goUpNext) {
                                    true -> { // Next : LeftUp
                                    }
                                    false -> { // Next : LeftDown
                                        createAnimation(
                                            R.animator.animator_face_shape_down,
                                            binding.ivSnowmanFace
                                        ).start()
                                        faceLeftDownAnimator.start()
                                    }
                                }
                            }
                        }
                    }
                })
            }
        faceUpFromBodyAnim.start()
    }

    private fun getAnimator(goRightNext: Boolean, goUpNext: Boolean, path: Path) =
        ObjectAnimator.ofFloat(binding.ivSnowmanFace, View.X, View.Y, path).apply {
            if (goUpNext) {
                duration = 2000
                interpolator = BounceInterpolator()
            } else {
                duration = 1500
                interpolator = DecelerateInterpolator(1.5f)
            }

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    when (goRightNext) {
                        true -> {
                            when (goUpNext) {
                                true -> { // Next : RightUp
                                    createAnimation(
                                        R.animator.animator_face_shape_up,
                                        binding.ivSnowmanFace
                                    ).start()
                                    faceRightUpAnimator.start()
                                }
                                false -> { // Next : RightDown
                                    setFaceDownToBodyAnim(true, true)
                                }
                            }
                        }
                        false -> {
                            when (goUpNext) {
                                true -> { // Next : LeftUp
                                    createAnimation(
                                        R.animator.animator_face_shape_up,
                                        binding.ivSnowmanFace
                                    ).start()
                                    faceLeftUpAnimator.start()
                                }
                                false -> { // Next : LeftDown
                                    setFaceDownToBodyAnim(false, true)
                                }
                            }
                        }
                    }
                }
            })
        }

    private fun createAnimation(animatorResId: Int, target: View): Animator {
        val anim = AnimatorInflater
            .loadAnimator(
                this,
                animatorResId
            ).apply {
                setTarget(target)
            }
        return anim
    }

    private fun translateDpToPx(dp: Int) =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        )
}
