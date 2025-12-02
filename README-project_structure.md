# Prolog

I know, this repo has a weird structure. When I encounter something like this, I always wonder if it was created by someone who knows what he doing and this is a result of careful consideration and the result is the optimal state of a project, or if it was created by an intern who doesn't have a full understanding of the consequences of his decisions and I should question everything he does.

Well, this is probably the latter. However, I've tried to think it through as much as possible. And, I wanted to experiment. Also, maybe when I learn more about Gradle and IJ, I'll be able to setup things properly. But now I just don't and I need to get some work done. With the current setup, I'm pretty confident I know what is going on.

# Description

Most notably, the Git root is separate from the Gradle root. You might ask where is the project root (from IJ perspective) and the answer is that it's up to you. This project doesn't contain any `.idea` files/dirs. So, you may want to open this a single big project where Gradle root doesn't match project root or multiple small projects where project root doesn't match Git root. In either way, something will probably be broken and will require some manual configuration.

# Rationale

This is an experimental project. This is not an application but a lot of different pieces of code that run other code, process data or are being measured. I need to be able to change things quickly.

## Gradle is broken

This project needs complicated and different Gradle settings for every part. I tried setting it up, but Gradle is very hard and confusing. I need very little dependencies and features that a single Gradle project would provide were not useful for me.

The parts interacted in an unpredictible way. Configuration applied in one part also applied in another part. Even when this is unlikely or caused by my lack of understanding, this is a problem. When I measure something, I need to be completely sure I measure what I want to measure. I just can't handle another source of unknown in my data.

The Gradle build took ages. Duh.

Things broke very often. When one part (which I didn't care about at the moment) broke, the whole build failed and I was just commenting out whole modules.

## Git submodules are broken

Git submodules are just so complicated to use. And I believe I understand them alright. I could separate this into multiple Git repos but then I would need to deal with Git submodules in a complicated way. Monorepo is just so much easier to use.

## IJ projects are broken

There are very often problems with Gradle integration in IJ. Things randomly work and then don't. Running stuff from CLI works everytime. 

The same goes with Kotlin notebook. I've filed multiple bug reports and I've spent countless hours trying to get IJ to work. 

IJ creates a lot of files in repo, commits them without my knowledge and I don't know what is going on. 

From my perspective, IJ is a very complex tool that is too smart for me. It doesn't try to explain to me how it's working. Which is alright when everything works. In other cases, I need to understand what is going under the hood which I just don't.
