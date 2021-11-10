package com.yjooooo.sampleanimation

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.graphics.Path
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import com.yjooooo.sampleanimation.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    var beforeCycle = true
    private lateinit var faceInfo: ViewInfo
    private lateinit var bodyInfo: ViewInfo
    private lateinit var faceRightUpAnimator: AnimatorSet
    private lateinit var faceRightDownAnimator: AnimatorSet
    private lateinit var faceLeftUpAnimator: AnimatorSet
    private lateinit var faceLeftDownAnimator: Animator
    private lateinit var faceDownToBodyAnim: Animator
    private lateinit var faceUpFromBodyAnim: Animator
    private lateinit var faceDownShapeAnim: Animator
    private lateinit var faceUpShapeAnim: Animator
    private lateinit var faceResetShapeAnim: Animator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setShapeAnimator()
        setOnButtonClickListener()
        setSnowmanInfo()
    }

    private fun setSnowmanInfo() {
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

    private fun setOnButtonClickListener() {
        binding.btnMain.setOnClickListener {
            binding.ivSnowmanFace.setImageResource(R.drawable.img_snowman_face_circle_eyes)
            beforeCycle = true
            setHorizontalAnimator()
            faceUpShapeAnim.start()
            faceRightUpAnimator.start()
        }
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

    private fun setShapeAnimator() {
        faceDownShapeAnim = createAnimation(
            R.animator.animator_face_shape_down,
            binding.ivSnowmanFace
        )
        faceUpShapeAnim = createAnimation(
            R.animator.animator_face_shape_up,
            binding.ivSnowmanFace
        )
        faceResetShapeAnim = createAnimation(
            R.animator.animator_face_shape_reset,
            binding.ivSnowmanFace
        ).apply {
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                }
            })
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

        val propertyRightUp = PropertyValuesHolder.ofFloat(View.ROTATION, -180f, 0f)
        val propertyRightDown = PropertyValuesHolder.ofFloat(View.ROTATION, 0f, 180f)
        val propertyLeftUp = PropertyValuesHolder.ofFloat(View.ROTATION, 180f, 0f)

        val rotationRightUpAnimator = getAnimator(goUpNext = false, propertyRightUp)
        val rotationRightDownAnimator = getAnimator(goUpNext = true, propertyRightDown)
        val rotationLeftUpAnimator = getAnimator(goUpNext = false, propertyLeftUp)

        faceRightUpAnimator = AnimatorSet().apply {
            play(getAnimator(goRightNext = true, goUpNext = false, path = rightUpPath))
                .with(rotationRightUpAnimator)
        }
        faceRightDownAnimator = AnimatorSet().apply {
            play(getAnimator(goRightNext = false, goUpNext = true, path = rightDownPath))
                .with(rotationRightDownAnimator)
        }
        faceLeftUpAnimator = AnimatorSet().apply {
            play(getAnimator(goRightNext = false, goUpNext = false, path = leftUpPath))
                .with(rotationLeftUpAnimator)
        }

        faceLeftDownAnimator = getAnimator(goRightNext = true, goUpNext = true, path = leftDownPath)

        // ObjectAnimator 눈사람 얼굴 이동 애니메이션의 기준이되는 사각형 확인해보는 코드
//        View(this).apply {
//            setBackgroundColor(Color.parseColor("#aa000000"))
//            x = left
//            y = top
//            layoutParams = ViewGroup.LayoutParams((right-left).toInt(),(bottom-top).toInt())
//            binding.container.addView(this)
//        }
    }

    private fun setFaceDownToBodyAnim(goRightNext: Boolean) {
        faceDownToBodyAnim =
            createAnimation(R.animator.animator_face_down_to_body, binding.ivSnowmanFace).apply {
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        when (goRightNext) {
                            true -> {
                                if (beforeCycle) {
                                    setFaceUpFromBodyAnim()
                                    beforeCycle = false
                                }
                            }
                            false -> {
                                faceResetShapeAnim.start()
                                disappearFace()
                            }
                        }
                    }
                })
            }
        faceDownToBodyAnim.start()
    }

    private fun setFaceUpFromBodyAnim() {
        faceUpFromBodyAnim =
            createAnimation(R.animator.animator_face_up_from_body, binding.ivSnowmanFace).apply {
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        faceRightDownAnimator.start()
                        faceDownShapeAnim.start()
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
                                    faceUpShapeAnim.start()
                                    faceRightUpAnimator.start()
                                }
                                false -> { // Next : RightDown
                                    setFaceDownToBodyAnim(goRightNext = true)
                                }
                            }
                        }
                        false -> {
                            when (goUpNext) {
                                true -> { // Next : LeftUp
                                    faceUpShapeAnim.start()
                                    faceLeftUpAnimator.start()
                                }
                                false -> { // Next : LeftDown
                                    setFaceDownToBodyAnim(goRightNext = false)
                                }
                            }
                        }
                    }
                }
            })
        }

    private fun getAnimator(goUpNext: Boolean, property: PropertyValuesHolder) =
        ObjectAnimator.ofPropertyValuesHolder(binding.ivSnowmanFace, property).apply {
            duration = 600
        }

    private fun showFace() {
        binding.ivSnowmanFace.apply {
            alpha = 0f
            animate()
                .alpha(1f)
                .setDuration(500)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        showTopButton()
                    }
                })
        }
    }

    private fun disappearFace() {
        binding.ivSnowmanFace.apply {
            animate()
                .alpha(0f)
                .setDuration(500)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        this@apply.setImageResource(R.drawable.img_snowman_face)
                        showFace()
                    }
                })
        }
    }

    private fun showTopButton() {
        binding.ivSnowmanButtonTop.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .setDuration(500)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        showMiddleButton()
                    }
                })
        }
    }

    private fun showMiddleButton() {
        binding.ivSnowmanButtonMiddle.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .setDuration(500)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        showBottomButton()
                    }
                })
        }
    }

    private fun showBottomButton() {
        binding.ivSnowmanButtonBottom.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .setDuration(500)
                .setListener(null)
        }
    }
}
