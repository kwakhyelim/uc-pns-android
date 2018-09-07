/*  Created by Edward Akoto on 12/31/12.
 *  Email akotoe@aua.ac.ke
 * 	Free for modification and distribution
 */

package ac.uc.mobile.pns.util;

import android.view.Gravity;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

public class OpenAnimation extends TranslateAnimation implements
		Animation.AnimationListener {

	private FrameLayout mainLayout;
	int panelWidth;
	int screenWidth;

	public OpenAnimation(FrameLayout layout, int width, int screenWidth, int fromXType,
			float fromXValue, int toXType, float toXValue, int fromYType,
			float fromYValue, int toYType, float toYValue) {

		super(fromXType, fromXValue, toXType, toXValue, fromYType, fromYValue,
				toYType, toYValue);

		// init
		this.screenWidth = screenWidth;
		mainLayout = layout;
		panelWidth = width;
		setDuration(200);
		setFillAfter(false);
		setInterpolator(new AccelerateDecelerateInterpolator());
		setAnimationListener(this);
		mainLayout.startAnimation(this);
	}

	public void onAnimationEnd(Animation arg0) {

		LayoutParams params = (LayoutParams) mainLayout.getLayoutParams();
		params.width = this.screenWidth;
		params.leftMargin = panelWidth;
		params.gravity = Gravity.LEFT;
		mainLayout.clearAnimation();
		mainLayout.setLayoutParams(params);
		mainLayout.requestLayout();

	}

	public void onAnimationRepeat(Animation arg0) {

	}

	public void onAnimationStart(Animation arg0) {

	}

}
