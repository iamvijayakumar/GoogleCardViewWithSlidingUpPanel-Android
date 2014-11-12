package com.vj.slidinguppanel.demo;

import android.os.Bundle;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;

import android.widget.ListView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.TextView;

import com.nhaarman.listviewanimations.ArrayAdapter;

import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.SwipeDismissAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;

public class GoogleCardsActivity extends Activity implements OnDismissCallback {

	private static final int INITIAL_DELAY_MILLIS = 100;

	private GoogleCardsAdapter mGoogleCardsAdapter;
	// private SlidingDrawer drawer;
	// private Button handle, clickMe;
	private static final String TAG = "GoogleCardsActivity";

	private SlidingUpPanelLayout mLayout;

	boolean itsOpend = false;
	int index = 0;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_googlecards);

		ListView listView = (ListView) findViewById(R.id.activity_googlecards_listview);
		mGoogleCardsAdapter = new GoogleCardsAdapter(this);
		SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
				new SwipeDismissAdapter(mGoogleCardsAdapter, this));
		swingBottomInAnimationAdapter.setAbsListView(listView);
		assert swingBottomInAnimationAdapter.getViewAnimator() != null;
		swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(
				INITIAL_DELAY_MILLIS);
		listView.setAdapter(swingBottomInAnimationAdapter);

		mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

		mLayout.setPanelSlideListener(new PanelSlideListener() {
			@Override
			public void onPanelSlide(View panel, float slideOffset) {
				Log.i(TAG, "onPanelSlide, offset " + slideOffset);

			}

			@Override
			public void onPanelExpanded(View panel) {
				Log.i(TAG, "onPanelExpanded");
				itsOpend = true;
				mGoogleCardsAdapter.notifyDataSetChanged();
				index = 0;
			}

			@Override
			public void onPanelCollapsed(View panel) {
				Log.i(TAG, "onPanelCollapsed");
				itsOpend = false;
				mGoogleCardsAdapter.notifyDataSetChanged();
				index = 0;
			}

			@Override
			public void onPanelAnchored(View panel) {
				Log.i(TAG, "onPanelAnchored");
				// itsOpend = true;
				// mGoogleCardsAdapter.notifyDataSetChanged();
			}

			@Override
			public void onPanelHidden(View panel) {
				Log.i(TAG, "onPanelHidden");
			}
		});

		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				Log.e("Log State", "scrollState :: " + scrollState);
				index = 0;
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				index = -1;
			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mLayout != null) {
					if (!itsOpend) {
						// mLayout.setAnchorPoint(0.7f);
						mLayout.expandPanel();

					} else {
						// mLayout.setAnchorPoint(1.0f);
						mLayout.collapsePanel();

					}
				}

			}
		});

		for (int i = 0; i < 25; i++) {
			mGoogleCardsAdapter.add(i);
		}

	}

	@Override
	public void onDismiss(final ViewGroup listView,
			final int[] reverseSortedPositions) {
		for (int position : reverseSortedPositions) {
			mGoogleCardsAdapter.remove(position);
		}
	}

	
	public class GoogleCardsAdapter extends ArrayAdapter<Integer> {

		private final Context mContext;
		private final BitmapCache mMemoryCache;

		GoogleCardsAdapter(final Context context) {
			mContext = context;
			mMemoryCache = new BitmapCache();
		}

		@Override
		public View getView(final int position, final View convertView,
				final ViewGroup parent) {
			final ViewHolder viewHolder;
			View view = convertView;
			if (view == null) {
				view = LayoutInflater.from(mContext).inflate(
						R.layout.activity_googlecards_card, parent, false);

				viewHolder = new ViewHolder();
				viewHolder.textView = (TextView) view
						.findViewById(R.id.activity_googlecards_card_textview);
				view.setTag(viewHolder);

				viewHolder.imageView = (ImageView) view
						.findViewById(R.id.activity_googlecards_card_imageview);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			if (itsOpend) {

				expand(viewHolder.imageView);

			} else {

				collapse(viewHolder.imageView);

			}

			viewHolder.textView.setText(mContext.getString(
					R.string.card_number, getItem(position) + 1));
			setImageView(viewHolder, position);

			return view;
		}

		private void setImageView(final ViewHolder viewHolder,
				final int position) {
			int imageResId;
			switch (getItem(position) % 5) {
			case 0:
				imageResId = R.drawable.a1;
				break;
			case 1:
				imageResId = R.drawable.a3;
				break;
			case 2:
				imageResId = R.drawable.a2;
				break;
			case 3:
				imageResId = R.drawable.a1;
				break;
			default:
				imageResId = R.drawable.a3;
			}

			Bitmap bitmap = getBitmapFromMemCache(imageResId);
			if (bitmap == null) {
				bitmap = BitmapFactory.decodeResource(mContext.getResources(),
						imageResId);
				addBitmapToMemoryCache(imageResId, bitmap);
			}
			viewHolder.imageView.setImageBitmap(bitmap);
		}

		private void addBitmapToMemoryCache(final int key, final Bitmap bitmap) {
			if (getBitmapFromMemCache(key) == null) {
				mMemoryCache.put(key, bitmap);
			}
		}

		private Bitmap getBitmapFromMemCache(final int key) {
			return mMemoryCache.get(key);
		}

		@SuppressWarnings({ "PackageVisibleField",
				"InstanceVariableNamingConvention" })
		private class ViewHolder {
			TextView textView;
			ImageView imageView;
		}

		public void expand(final View v) {
			v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			final int targetHeight = v.getMeasuredHeight();

			v.getLayoutParams().height = 0;
			v.setVisibility(View.VISIBLE);
			Animation a = new Animation() {
				@Override
				protected void applyTransformation(float interpolatedTime,
						Transformation t) {
					v.getLayoutParams().height = interpolatedTime == 1 ? LayoutParams.WRAP_CONTENT
							: (int) (targetHeight * interpolatedTime);
					v.requestLayout();
				}

				@Override
				public boolean willChangeBounds() {
					return true;
				}
			};

			// 1dp/ms
			a.setDuration((int) (targetHeight / v.getContext().getResources()
					.getDisplayMetrics().density));
			v.startAnimation(a);
		}

		public void collapse(final View v) {
			final int initialHeight = v.getMeasuredHeight();

			Animation a = new Animation() {
				@Override
				protected void applyTransformation(float interpolatedTime,
						Transformation t) {
					if (interpolatedTime == 1) {
						v.setVisibility(View.GONE);
					} else {
						v.getLayoutParams().height = initialHeight
								- (int) (initialHeight * interpolatedTime);
						v.requestLayout();
					}
				}

				@Override
				public boolean willChangeBounds() {
					return true;
				}
			};

			// 1dp/ms
			a.setDuration((int) (initialHeight / v.getContext().getResources()
					.getDisplayMetrics().density));
			v.startAnimation(a);
		}
	}

	
}
