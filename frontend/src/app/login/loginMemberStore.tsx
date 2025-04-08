"use client";

import {
  createContext,
  useState,
  useContext,
  useEffect,
  ReactNode,
} from "react";
import { components } from "@/lib/backend/apiV1/schema";
import client from "@/lib/backend/client";

type Member = components["schemas"]["MemberDto"];

// 타입 정의: null 허용
export const LoginMemberContext = createContext<{
  loginMember: Member | null;
  setLoginMember: (member: Member | null) => void;
  removeLoginMember: () => void;
  isLogin: boolean;
  isLoginMemberPending: boolean;
  isAdmin: boolean;
  setNoLoginMember: () => void;
}>({
  loginMember: null,
  setLoginMember: () => {},
  removeLoginMember: () => {},
  isLogin: false,
  isLoginMemberPending: true,
  isAdmin: false,
  setNoLoginMember: () => {},
});

async function checkIsAdmin(id: number): Promise<boolean> {
  try {
    const response = await client.GET("/member/{id}/isAdmin", {
      params: {
        path: {
          id,
        },
      },
    });

    if (!response.error && response.data) {
      return response.data.data || false;
    }
    return false;
  } catch {
    return false;
  }
}

export function useLoginMember() {
  const [isLoginMemberPending, setLoginMemberPending] = useState(true);
  const [loginMember, _setLoginMember] = useState<Member | null>(null);
  const [isAdmin, setIsAdmin] = useState(false);

  useEffect(() => {
    if (loginMember?.id) {
      checkIsAdmin(loginMember.id).then(setIsAdmin);
    } else {
      setIsAdmin(false);
    }
  }, [loginMember?.id]);

  const removeLoginMember = () => {
    _setLoginMember(null);
    setLoginMemberPending(false);
    setIsAdmin(false);
  };

  const setLoginMember = (member: Member | null) => {
    if (!member || !member.id) {
      removeLoginMember();
      return;
    }

    _setLoginMember(member);
    setLoginMemberPending(false);
    checkIsAdmin(member.id).then(setIsAdmin);
  };

  const setNoLoginMember = () => {
    setLoginMemberPending(false);
  };

  const isLogin = loginMember !== null;

  return {
    loginMember,
    removeLoginMember,
    isLogin,
    isLoginMemberPending,
    setLoginMember,
    isAdmin,
    setNoLoginMember,
  };
}

export function useLoginMemberContext() {
  return useContext(LoginMemberContext);
}
