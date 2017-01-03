package com.anniu.shandiandaojia.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;

public class MyDialog extends Dialog {
	
	public MyDialog(Context context) {
		super(context);
	}

	public MyDialog(Context context, int theme) {
		super(context, theme);
	}

	public static class Builder {
		private Context context;
		private String title;
		private String message;
		private String confirmButtonText;
		private String cancelButtonText;
		private View contentView;
		private boolean cancelable;
		private DialogInterface.OnClickListener confirmButtonClickListener;
		private DialogInterface.OnClickListener cancelButtonClickListener;

		public Builder(Context context) {
			this.context = context;
		}

		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}

		/**
		 * Set the Dialog message from resource
		 * 
		 * @param title
		 * @return
		 */
		public Builder setMessage(int message) {
			this.message = (String) context.getText(message);
			return this;
		}

		/**
		 * Set the Dialog title from resource
		 * 
		 * @param title
		 * @return
		 */
		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}

		/**
		 * Set the Dialog title from String
		 * 
		 * @param title
		 * @return
		 */

		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder setContentView(View v) {
			this.contentView = v;
			return this;
		}
		
		public void setCancelable(boolean flag) {
			this.cancelable = flag;
		}
		

		/**
		 * Set the positive button resource and it's listener
		 * 
		 * @param confirmButtonText
		 * @return
		 */
		public Builder setConfirmButton(int confirmButtonText,
				DialogInterface.OnClickListener listener) {
			this.confirmButtonText = (String) context
					.getText(confirmButtonText);
			this.confirmButtonClickListener = listener;
			return this;
		}

		public Builder setConfirmButton(String confirmButtonText,
				DialogInterface.OnClickListener listener) {
			this.confirmButtonText = confirmButtonText;
			this.confirmButtonClickListener = listener;
			return this;
		}

		public Builder setCancelButton(int cancelButtonText,
				DialogInterface.OnClickListener listener) {
			this.cancelButtonText = (String) context
					.getText(cancelButtonText);
			this.cancelButtonClickListener = listener;
			return this;
		}

		public Builder setCancelButton(String cancelButtonText,
				DialogInterface.OnClickListener listener) {
			this.cancelButtonText = cancelButtonText;
			this.cancelButtonClickListener = listener;
			return this;
		}
		
		public MyDialog create() {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final MyDialog dialog = new MyDialog(context,R.style.MyDialog);
			dialog.setCancelable(cancelable);
			View layout = inflater.inflate(R.layout.dialog_layout, null);
			dialog.addContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			
			if (title != null) {
				((TextView) layout.findViewById(R.id.title)).setText(title);
			} else {
				layout.findViewById(R.id.title).setVisibility(View.GONE);
			}
			if (confirmButtonText != null) {
				((Button) layout.findViewById(R.id.confirmButton))
						.setText(confirmButtonText);
				if (confirmButtonClickListener != null) {
					((Button) layout.findViewById(R.id.confirmButton))
							.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									confirmButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_POSITIVE);
								}
							});
				}
			} else {
				layout.findViewById(R.id.confirmLaytout).setVisibility(View.GONE);
			}
			if (cancelButtonText != null) {
				((Button) layout.findViewById(R.id.cancelButton))
						.setText(cancelButtonText);
				if (cancelButtonClickListener != null) {
					((Button) layout.findViewById(R.id.cancelButton))
							.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									cancelButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_NEGATIVE);
								}
							});
				}
			} else {
				layout.findViewById(R.id.cancelLaytout).setVisibility(
						View.GONE);
			}
			if (message != null) {
				((TextView) layout.findViewById(R.id.message)).setText(message);
			} else if (contentView != null) {
				((LinearLayout) layout.findViewById(R.id.content))
						.removeAllViews();
				((LinearLayout) layout.findViewById(R.id.content)).addView(
						contentView, new LayoutParams(
								LayoutParams.FILL_PARENT,
								LayoutParams.FILL_PARENT));
			}
			dialog.setContentView(layout);
			return dialog;
		}

	}
}
