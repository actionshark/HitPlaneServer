package com.kk.hitplane.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.kk.hitplane.Response;
import com.kk.hitplane.database.UserInfoDB;
import com.kk.hitplane.request.Call;
import com.kk.hitplane.response.GameCall;
import com.kk.hitplane.response.GameEnd;
import com.kk.hitplane.response.GameHand;
import com.kk.hitplane.response.RoomInfo;
import com.kk.hitplane.response.GameLose;
import com.kk.hitplane.response.GameTurn;

public class Room {
	private static int sCount = 0;

	/////////////////////////////////////////////////////////////////////

	public class User {
		public int id;
		public int money = 0;
		public boolean ready = false;

		public final List<Card> cards = new ArrayList<>();
		public int score;

		public int wager;
		public boolean alive;

		public final Set<Integer> watch = new HashSet<>();

		public void wage() {
			int value = mWagerCurr;

			money -= value;
			wager += value;
			mWagerTotal += value;
		}
	}

	public final int id;

	private int mNumMin = 2;
	private int mNumMax = 4;

	private final List<User> mUsers = new ArrayList<>();

	private boolean mIsRunning = false;

	private int mBanker = 0;
	private int mIndex;

	private int mWagerFirst = 1;
	private int mWagerCurr;

	private int mWagerTotal;
	private int mWagerMax = 100;

	private Deck mDeck = new Deck();

	protected Room() {
		id = ++sCount;
	}

	public synchronized boolean isRunning() {
		return mIsRunning;
	}

	public synchronized int getWagerCurr() {
		return mWagerCurr;
	}

	public synchronized int getWagerTotal() {
		return mWagerTotal;
	}

	public synchronized int getCurrent() {
		return mUsers.get(mIndex).id;
	}

	public synchronized List<User> getUsers() {
		return new ArrayList<>(mUsers);
	}

	public synchronized String watchHand(int ob, int actor) {
		if (!mIsRunning) {
			return "游戏未开始";
		}

		for (User user : mUsers) {
			if (user.id == ob) {
				if (user.watch.contains(actor)) {
					return null;
				} else {
					return "无法查看该玩家手牌";
				}
			}
		}

		return "没有找到该玩家";
	}

	protected synchronized String join(int id) {
		if (mIsRunning) {
			return "游戏正在进行中";
		}

		if (mUsers.size() >= mNumMax) {
			return "人数已满";
		}

		for (User user : mUsers) {
			if (user.id == id) {
				return "已经在房间里了";
			}
		}

		User user = new User();
		user.id = id;

		mUsers.add(user);

		return null;
	}

	public synchronized String quit(int id) {
		for (User user : mUsers) {
			if (user.id == id) {
				if (mIsRunning && user.alive) {
					return "游戏正在进行中";
				}

				mUsers.remove(user);
				return null;
			}
		}

		return "不在房间中";
	}

	public synchronized String ready(int id) {
		if (mIsRunning) {
			return "游戏正在进行中";
		}

		for (User user : mUsers) {
			if (user.id == id) {
				if (user.ready) {
					return "已经准备了";
				} else {
					user.ready = true;
					return null;
				}
			}
		}

		return "不在房间中";
	}

	public synchronized String unready(int id) {
		if (mIsRunning) {
			return "游戏正在进行中";
		}

		for (User user : mUsers) {
			if (user.id == id) {
				if (user.ready) {
					user.ready = false;
					return null;
				} else {
					return "未准备";
				}
			}
		}

		return "不在房间中";
	}

	protected synchronized String begin() {
		if (mIsRunning) {
			return "游戏在进行中";
		}

		if (mUsers.size() < mNumMin) {
			return "人数不够";
		}

		for (User user : mUsers) {
			if (!user.ready) {
				return "没有全部准备";
			}
		}

		mIsRunning = true;

		mIndex = -1;

		for (int i = 0; i < mUsers.size(); i++) {
			if (mBanker == mUsers.get(i).id) {
				mIndex = i;
				break;
			}
		}

		mIndex = (mIndex + 1) % mUsers.size();
		mBanker = mUsers.get(mIndex).id;

		mWagerCurr = mWagerFirst;
		mWagerTotal = 0;

		mDeck.reset();
		mDeck.shuffle();

		for (User user : mUsers) {
			user.alive = true;

			user.cards.clear();
			for (int i = 0; i < Deck.HAND_NUM; i++) {
				user.cards.add(mDeck.deal());
			}
			user.score = Deck.calcScore(user.cards);

			user.wager = 0;
			user.wage();

			user.watch.clear();
		}

		RoomInfo ri = new RoomInfo();
		ri.encode(this, 0);
		sendAll(ri);

		mIndex--;
		nextStep();

		return null;
	}

	public synchronized String call(int id, int type, int pk) {
		if (!mIsRunning) {
			return "游戏未开始";
		}

		User actor = null;

		for (User user : mUsers) {
			if (user.id == id) {
				actor = user;
				break;
			}
		}

		if (actor == null) {
			return "不在此房间";
		}

		if (mUsers.get(mIndex).id != id) {
			return "不是你的回合";
		}

		if (type == Call.TYPE_GIVEUP) {
			actor.alive = false;

			GameLose gl = new GameLose();
			sendAll(gl);

			nextStep();
			return null;
		}

		if (type == Call.TYPE_NORMAL) {
			actor.wage();

			GameCall gc = new GameCall();
			sendAll(gc);

			nextStep();
			return null;
		}

		if (type == Call.TYPE_DOUBLE) {
			User pker = null;

			if (pk != 0) {
				if (pk == actor.id) {
					return "不能和自己比牌";
				}

				for (User user : mUsers) {
					if (pk == user.id) {
						pker = user;
						break;
					}
				}

				if (!pker.alive) {
					return "该玩家已经出局";
				}
			}

			mWagerCurr *= 2;
			actor.wage();

			GameCall gc = new GameCall();
			sendAll(gc);

			if (pker != null) {
				actor.watch.add(pker.id);
				pker.watch.add(actor.id);

				GameHand gh = new GameHand();
				gh.send(actor.id);
				gh.send(pker.id);

				if (actor.score > pker.score) {
					pker.alive = false;
				} else {
					actor.alive = false;
				}

				GameLose gl = new GameLose();
				sendAll(gl);
			}

			nextStep();
			return null;
		}

		return "非法操作";
	}

	private void sendAll(Response res) {
		for (User user : mUsers) {
			res.send(user.id);
		}
	}

	private synchronized void nextStep() {
		if (checkEnd()) {
			return;
		}

		for (mIndex++;; mIndex++) {
			mIndex %= mUsers.size();

			User user = mUsers.get(mIndex);
			if (user.alive) {
				break;
			}
		}

		GameTurn tc = new GameTurn();
		sendAll(tc);
	}

	private synchronized boolean checkEnd() {
		if (!isEnd()) {
			return false;
		}

		List<User> users = new ArrayList<>();

		for (User user : mUsers) {
			if (user.alive) {
				users.add(user);
			}
		}

		if (users.size() > 1) {
			users.sort(new Comparator<User>() {
				@Override
				public int compare(User a, User b) {
					return b.score - a.score;
				}
			});
		}

		User winner = users.get(0);
		int total = 0;

		for (User user : mUsers) {
			if (user.id != winner.id) {
				UserInfoDB.changeMoney(user.id, -user.wager);
				total += user.wager;
			}
		}

		UserInfoDB.changeMoney(winner.id, total);
		winner.money += total + winner.wager;

		GameEnd ge = new GameEnd();
		sendAll(ge);

		mIsRunning = false;

		for (User user : mUsers) {
			user.ready = false;
		}

		return true;
	}

	private boolean isEnd() {
		if (mWagerTotal >= mWagerMax) {
			return true;
		}

		int count = 0;

		for (User user : mUsers) {
			if (user.alive) {
				count++;
			}
		}

		return count <= 1;
	}
}
