package org.example.sesstion;

import org.example.model.User;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class SessionManager {
    // 세션 저장소 (sessionId -> Session)
    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();
    // 세션 만료 시간 (밀리초 단위)
    private final long sessionTimeoutMillis;
    // 만료된 세션을 주기적으로 정리하는 스케줄러
    private final ScheduledExecutorService cleanupScheduler =
            Executors.newSingleThreadScheduledExecutor();

    /**
     * @param sessionTimeoutMillis  세션 만료 기간 (예: 30분 -> 30 * 60 * 1000)
     * @param cleanupIntervalMillis 만료된 세션 정리 주기 (예: 5분)
     */
    public SessionManager(long sessionTimeoutMillis, long cleanupIntervalMillis) {
        this.sessionTimeoutMillis = sessionTimeoutMillis;
        // 일정 주기로 만료된 세션 삭제
        cleanupScheduler.scheduleAtFixedRate(
                this::cleanupExpiredSesstions,
                cleanupIntervalMillis,
                cleanupIntervalMillis,
                TimeUnit.MILLISECONDS
        );
    }

    /**
     * 세션 생성 (로그인 성공 후 호출)
     * @return 생성된 sessionId
     */
    public String createSession(User user) {
        String sessionId = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();
        sessions.put(sessionId, new Session(user, now, now));
        return sessionId;
    }

    /**
     * 세션 유효성 검사
     * @return 유효하면, true, 만료되었거나 없음 -> false
     */
    public boolean isValidSession(String sessionId) {
        Session session = sessions.get(sessionId);
        if (session == null) {
            return false;
        }
        long now = System.currentTimeMillis();
        // 마지막 접근 후 일정 시간 경과 시 만료 처리
        if (now - session.getLastAccessedTime() > sessionTimeoutMillis) {
            sessions.remove(sessionId);
            return false;
        }
        // 접근 시점 갱신
        session.setLastAccessedTime(now);
        return true;
    }

    /**
     * 세션에 저장된 유저 정보 반환
     * @return User 객체, 없으면 null
     */
    public User getUser(String sessionId) {
        Session session = sessions.get(sessionId);
        return (session != null) ? session.getUser() : null;
    }

    /**
     * 세션 삭제(로그아우스 처리)
     */
    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }

    /**
     * 만료된 세션을 맵에서 제거
     */
    private void cleanupExpiredSesstions() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<String, Session>> it = sessions.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Session> entry = it.next();
            if (now - entry.getValue().getLastAccessedTime() > sessionTimeoutMillis) {
                it.remove();
            }
        }
    }

    /**
     * 애플리케이션 종료 시 스케줄러도 함께 종료
     */
    public void shutdown() {
        cleanupScheduler.shutdown();
    }

    // 세선 정보 저장용 내부 클래스
    private static class Session {
        private final User user;
        private final long creationTime;
        private long lastAccessedTime;

        public Session(User user, long creationTime, long lastAccessedTime) {
            this.user = user;
            this.creationTime = creationTime;
            this.lastAccessedTime = lastAccessedTime;
        }

        public User getUser() {
            return user;
        }

        public long getCreationTime() {
            return creationTime;
        }

        public long getLastAccessedTime() {
            return lastAccessedTime;
        }

        public void setLastAccessedTime(long lastAccessedTime) {
            this.lastAccessedTime = lastAccessedTime;
        }
    }
}
