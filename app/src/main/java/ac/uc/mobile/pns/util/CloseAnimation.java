/*  Created by Edward Akoto on 12/31/12.
 *  Email akotoe@aua.ac.ke
 * 	Free for modification and distribution
 */

package ac.uc.mobile.pns.util;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;

public class CloseAnimation extends TranslateAnimation implements
		TranslateAnimation.AnimationListener {

	private FrameLayout mainLayout;
	int panelWidth;

	public CloseAnimation(FrameLayout layout, int width, int fromXType,
			float fromXValue, int toXType, float toXValue, int fromYType,
			float fromYValue, int toYType, float toYValue) {

		super(fromXType, fromXValue, toXType, toXValue, fromYType, fromYValue,
				toYType, toYValue);

		// Initialize
		mainLayout = layout;
		panelWidth = width;
		setDuration(200);
		setFillAfter(false);
		setInterpolator(new AccelerateDecelerateInterpolator());
		setAnimationListener(this);

		// Clear left and right margins
		LayoutParams params = (LayoutParams) mainLayout.getLayoutParams();
		params.rightMargin = 0;
		params.leftMargin = 0;
		mainLayout.setLayoutParams(params);
		mainLayout.requestLayout();
		mainLayout.startAnimation(this);

	}

	public void onAnimationEnd(Animation animation) {

	}

	public void onAnimationRepeat(Animation animation) {

	}

	public void onAnimationStart(Animation animation) {

	}

}
