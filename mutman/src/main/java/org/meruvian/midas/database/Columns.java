/*
 * Copyright 2012 Meruvian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meruvian.midas.database;

/**
 * @author Dian Aditya
 * 
 */
public class Columns {
	public interface PrayerColumns {
		public enum Reminder {
			_6_AM("_6_AM"), _12_PM("_12_PM"), _6_PM("_6_PM"), TEST("_10_AM");

			private String string;

			Reminder(String string) {
				this.string = string;
			}

			public static Reminder get(String reminder) {
				for (Reminder value : values()) {
					if (value.string.equals(reminder)) {
						return value;
					}
				}

				return null;
			}

			public static Reminder get(int ordinal) {
				return values()[ordinal];
			}
		}

		String TITLE = "title";
		String CONTENT = "content";
		String REMINDER = "reminder";
		String CREATE_DATE = "create_date";
		String SYNC_DATE = "sync_date";
		String ON_SERVER_ID = "on_server_id";
		String CATEGORY_ID = "category_id";
		String ALREADY_READ = "already_read";
		String TYPE = "type";
	}

	public interface PrayerCategoryColumns {
		String ON_SERVER_ID = "on_server_id";
		String NAME = "name";
		String DESCRIPTION = "description";
		String SYNC_DATE = "sync_date";
	}
}
