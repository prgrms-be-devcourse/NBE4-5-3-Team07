"use client";

import { ReactNode } from "react";
import { LoginMemberContext } from "./loginMemberStore";
import { useLoginMember } from "./loginMemberStore";

export default function LoginMemberProvider({
  children,
}: {
  children: ReactNode;
}) {
  const loginMemberState = useLoginMember();

  return (
    <LoginMemberContext.Provider value={loginMemberState}>
      {children}
    </LoginMemberContext.Provider>
  );
}
