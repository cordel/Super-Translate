package live.senya.supertranslate.schedulers

import rx.Scheduler
import rx.schedulers.Schedulers


class ImmediateSchedulerProvider : BaseSchedulerProvider {

    override fun computation(): Scheduler = Schedulers.immediate()

    override fun io(): Scheduler = Schedulers.immediate()

    override fun ui(): Scheduler = Schedulers.immediate()

}